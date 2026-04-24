(function (root, factory) {
    if (typeof module === 'object' && module.exports) {
        module.exports = factory(require('jszip'));
    } else {
        root.ApkParser = factory(root.JSZip);
    }
}(typeof self !== 'undefined' ? self : this, function (JSZip) {
    if (!JSZip) {
        throw new Error('JSZip is required');
    }

    const isNode = typeof process !== 'undefined' && process.versions && process.versions.node;
    const TextDecoderImpl = typeof TextDecoder !== 'undefined'
        ? TextDecoder
        : (isNode ? require('util').TextDecoder : null);

    function getDecoder(encoding) {
        if (!TextDecoderImpl) {
            throw new Error('TextDecoder is not available in this environment');
        }
        return new TextDecoderImpl(encoding);
    }

    async function parseApkFile(file, options = {}) {
        const arrayBuffer = await file.arrayBuffer();
        return parseApkBuffer(new Uint8Array(arrayBuffer), options);
    }

    async function parseApkBuffer(buffer, options = {}) {
        const debug = !!options.debug;
        if (debug) console.log('[APK] 开始解析 buffer，大小:', buffer.length);

        const zip = await JSZip.loadAsync(buffer);
        if (debug) console.log('[APK] ZIP条目数:', Object.keys(zip.files).length);

        const manifestFile = zip.file('AndroidManifest.xml');
        if (!manifestFile) {
            throw new Error('未找到AndroidManifest.xml文件');
        }

        const manifestData = await manifestFile.async('uint8array');
        if (debug) console.log('[APK] Manifest大小:', manifestData.length);

        const packageInfo = parseBinaryManifest(manifestData, debug);
        if (debug) console.log('[APK] 解析结果:', packageInfo);

        if (packageInfo.labelIsResourceId && packageInfo.labelResourceId) {
            const resourcesFile = zip.file('resources.arsc');
            if (resourcesFile) {
                const resourcesData = await resourcesFile.async('uint8array');
                const realLabel = getStringFromResources(resourcesData, packageInfo.labelResourceId, debug);
                if (realLabel) {
                    packageInfo.appName = realLabel;
                }
            }
        }

        return packageInfo;
    }

    async function parseApkPath(filePath, options = {}) {
        if (!isNode) {
            throw new Error('parseApkPath 仅支持 Node.js');
        }
        const fs = require('fs');
        const data = fs.readFileSync(filePath);
        return parseApkBuffer(new Uint8Array(data), options);
    }

    function parseBinaryManifest(data, debug) {
        const RES_XML_TYPE = 0x0003;
        const RES_STRING_POOL_TYPE = 0x0001;
        const RES_XML_START_NAMESPACE_TYPE = 0x0100;
        const RES_XML_END_NAMESPACE_TYPE = 0x0101;
        const RES_XML_START_ELEMENT_TYPE = 0x0102;
        const RES_XML_END_ELEMENT_TYPE = 0x0103;
        const RES_XML_CDATA_TYPE = 0x0104;

        let offset = 0;

        const fileType = readUint16(data, offset);
        const headerSize = readUint16(data, offset + 2);
        const fileSize = readUint32(data, offset + 4);
        if (fileType !== RES_XML_TYPE || headerSize !== 0x0008) {
            throw new Error('无效的AndroidManifest.xml格式');
        }
        if (debug) console.log('[APK] AXML header', { fileType, headerSize, fileSize });
        offset += headerSize;

        let stringPool = null;
        let packageName = null;
        let versionCode = null;
        let versionName = null;
        let appName = null;
        let labelIsResourceId = false;
        let labelResourceId = null;
        let minSdk = null;
        let targetSdk = null;
        const permissions = [];

        while (offset < data.length) {
            const chunkType = readUint16(data, offset);
            const chunkSize = readUint32(data, offset + 4);

            if (chunkSize === 0 || chunkSize > data.length - offset) {
                break;
            }

            switch (chunkType) {
                case RES_STRING_POOL_TYPE:
                    stringPool = parseStringPool(data, offset, debug);
                    offset += chunkSize;
                    break;

                case RES_XML_START_ELEMENT_TYPE:
                    if (stringPool) {
                        const tagInfo = parseStartTag(data, offset, stringPool, debug);
                        if (tagInfo.name === 'manifest') {
                            for (const attr of tagInfo.attributes) {
                                if (attr.name === 'package') {
                                    packageName = attr.value;
                                } else if (attr.name === 'versionCode') {
                                    versionCode = attr.value;
                                } else if (attr.name === 'versionName') {
                                    versionName = attr.value;
                                }
                            }
                        } else if (tagInfo.name === 'application') {
                            for (const attr of tagInfo.attributes) {
                                if (attr.name === 'label') {
                                    if (attr.value && attr.value.startsWith('@0x')) {
                                        labelIsResourceId = true;
                                        labelResourceId = parseInt(attr.value.substring(3), 16);
                                    } else if (attr.value) {
                                        appName = attr.value;
                                    }
                                } else if (attr.name === 'name' && !appName) {
                                    if (attr.value && !attr.value.startsWith('@') && !attr.value.startsWith('.')) {
                                        appName = attr.value;
                                    }
                                }
                            }
                        } else if (tagInfo.name === 'uses-sdk') {
                            for (const attr of tagInfo.attributes) {
                                if (attr.name === 'minSdkVersion') {
                                    minSdk = attr.value;
                                } else if (attr.name === 'targetSdkVersion') {
                                    targetSdk = attr.value;
                                }
                            }
                        } else if (tagInfo.name === 'uses-permission') {
                            for (const attr of tagInfo.attributes) {
                                if (attr.name === 'name') {
                                    const perm = attr.value.replace('android.permission.', '');
                                    permissions.push(perm);
                                }
                            }
                        }
                    }
                    offset += chunkSize;
                    break;

                case RES_XML_START_NAMESPACE_TYPE:
                case RES_XML_END_NAMESPACE_TYPE:
                case RES_XML_END_ELEMENT_TYPE:
                case RES_XML_CDATA_TYPE:
                    offset += chunkSize;
                    break;

                default:
                    offset += chunkSize;
            }
        }

        if (stringPool && !packageName) {
            for (const s of stringPool.strings) {
                if (s && s.match(/^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$/)) {
                    packageName = s;
                    break;
                }
            }
        }

        return {
            packageName: packageName || '未知',
            appName: appName || '未知应用',
            version: versionName || '1.0.0',
            versionCode: String(versionCode || '1'),
            minSdk: String(minSdk || '21'),
            targetSdk: String(targetSdk || '30'),
            permissions,
            labelIsResourceId,
            labelResourceId
        };
    }

    function parseStringPool(data, startOffset, debug) {
        const offset = startOffset;
        const stringCount = readUint32(data, offset + 8);
        const styleCount = readUint32(data, offset + 12);
        const flags = readUint32(data, offset + 16);
        const stringsStart = readUint32(data, offset + 20);

        const isUtf8 = (flags & (1 << 8)) !== 0;
        const strings = [];

        if (debug) {
            console.log('[APK] StringPool', { stringCount, styleCount, flags, stringsStart, isUtf8 });
        }

        const stringOffsets = [];
        for (let i = 0; i < stringCount; i++) {
            stringOffsets.push(readUint32(data, offset + 28 + i * 4));
        }

        const dataStart = offset + stringsStart;

        for (let i = 0; i < stringCount; i++) {
            try {
                const stringOffset = stringOffsets[i];
                const actualOffset = dataStart + stringOffset;

                if (actualOffset >= data.length) {
                    strings.push(null);
                    continue;
                }

                const str = isUtf8
                    ? readUtf8String(data, actualOffset)
                    : readUtf16String(data, actualOffset);
                strings.push(str);
            } catch (e) {
                strings.push(null);
            }
        }

        return { strings, stringsStart };
    }

    function readUtf8String(data, startOffset) {
        let pos = startOffset;
        const len = data.length;

        let charLen = data[pos++];
        if (charLen > 0x7f) {
            charLen = ((charLen & 0x7f) << 8) | data[pos++];
        }

        let byteLen = data[pos++];
        if (byteLen > 0x7f) {
            byteLen = ((byteLen & 0x7f) << 8) | data[pos++];
        }

        if (pos + byteLen > len) return null;

        const strData = data.slice(pos, pos + byteLen);
        return getDecoder('utf-8').decode(strData);
    }

    function readUtf16String(data, startOffset) {
        let charLen = readUint16(data, startOffset);
        if (charLen > 0x7fff) {
            charLen = ((charLen & 0x7fff) << 16) | readUint16(data, startOffset + 2);
        }

        const strStart = startOffset + 2;
        const byteLen = charLen * 2;
        if (strStart + byteLen > data.length) return null;

        const strData = data.slice(strStart, strStart + byteLen);
        return getDecoder('utf-16le').decode(strData);
    }

    function parseStartTag(data, offset, stringPool, debug) {
        const lineNumber = readUint32(data, offset + 8);
        const nameIndex = readUint32(data, offset + 20);
        const attributeSize = readUint16(data, offset + 26);
        const attributeCount = readUint16(data, offset + 28);

        const name = stringPool.strings[nameIndex] || '';
        const attributes = [];

        if (debug && (name === 'manifest' || name === 'application' || name === 'uses-sdk')) {
            console.log('[APK] Tag:', name, { attributeCount, attributeSize, nameIndex });
        }

        for (let i = 0; i < attributeCount; i++) {
            const attrOffset = offset + 36 + i * attributeSize;

            const attrNameIndex = readUint32(data, attrOffset + 4);
            const attrValueStringIndex = readUint32(data, attrOffset + 8);
            const attrDataType = data[attrOffset + 15];
            const attrData = readUint32(data, attrOffset + 16);

            const attrName = stringPool.strings[attrNameIndex] || '';
            let attrValue;

            if (attrValueStringIndex !== 0xFFFFFFFF) {
                attrValue = stringPool.strings[attrValueStringIndex] || '';
            } else {
                const type = attrDataType & 0xFF;
                if (type === 0x10) {
                    attrValue = String(attrData);
                } else if (type === 0x11) {
                    attrValue = '0x' + attrData.toString(16);
                } else if (type === 0x12) {
                    attrValue = attrData !== 0 ? 'true' : 'false';
                } else if (type === 0x03) {
                    attrValue = stringPool.strings[attrData] || '';
                } else {
                    attrValue = String(attrData);
                }
            }

            attributes.push({
                name: attrName,
                value: attrValue,
                type: attrDataType
            });
        }

        return {
            lineNumber,
            name,
            attributes
        };
    }

    function getStringFromResources(data, resourceId, debug) {
        if (debug) {
            console.log('[APK] resources.arsc 解析暂未实现', resourceId);
        }
        return null;
    }

    function readUint16(data, offset) {
        return data[offset] | (data[offset + 1] << 8);
    }

    function readUint32(data, offset) {
        return data[offset] | (data[offset + 1] << 8) | (data[offset + 2] << 16) | (data[offset + 3] << 24);
    }

    return {
        parseApkFile,
        parseApkBuffer,
        parseApkPath
    };
}));

if (typeof module === 'object' && module.exports && require.main === module) {
    const parser = module.exports;
    const filePath = process.argv[2];
    if (!filePath) {
        console.error('Usage: node apk-parser.js <apk-file>');
        process.exit(1);
    }

    parser.parseApkPath(filePath, { debug: true })
        .then((info) => {
            console.log(JSON.stringify(info, null, 2));
        })
        .catch((err) => {
            console.error(err.message);
            process.exit(1);
        });
}
