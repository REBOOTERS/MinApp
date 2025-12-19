#!/usr/bin/env python3
"""
Android Native Library Builder
支持通过命令行参数传入 NDK 路径
"""

import os
import sys
import shutil
import subprocess
import argparse
import platform
from pathlib import Path
from typing import List, Dict, Optional, Tuple

class AndroidLibraryBuilder:
    def __init__(self, ndk_path: Optional[str] = None, project_dir: Optional[str] = None):
        """
        初始化构建器

        Args:
            ndk_path: NDK 路径，如果为 None 则自动查找
            project_dir: 项目目录，如果为 None 则使用当前目录
        """
        self.project_dir = os.path.abspath(project_dir or os.getcwd())
        self.build_dir = os.path.join(self.project_dir, "build_android")
        self.jni_dir = os.path.join(self.project_dir, "jniLibs")
        self.is_windows = platform.system() == "Windows"

        # NDK 路径处理
        self.ndk_path = self._resolve_ndk_path(ndk_path)

        # 平台特定的配置
        self.generator = "Ninja"
        self.make_program = "ninja"

        # ABI 配置列表
        self.default_abis = [
            {"name": "armeabi-v7a", "api": 19, "description": "32-bit ARM"},
            {"name": "arm64-v8a", "api": 21, "description": "64-bit ARM"},
            {"name": "x86", "api": 19, "description": "32-bit x86"},
            {"name": "x86_64", "api": 21, "description": "64-bit x86"},
        ]

        # 颜色输出
        self._init_colors()

    def _init_colors(self):
        """初始化颜色输出"""
        self.use_colors = sys.stdout.isatty()
        if self.use_colors:
            self.GREEN = "\033[92m"
            self.RED = "\033[91m"
            self.YELLOW = "\033[93m"
            self.BLUE = "\033[94m"
            self.PURPLE = "\033[95m"
            self.CYAN = "\033[96m"
            self.RESET = "\033[0m"
            self.BOLD = "\033[1m"
        else:
            self.GREEN = self.RED = self.YELLOW = self.BLUE = ""
            self.PURPLE = self.CYAN = self.RESET = self.BOLD = ""

    def _resolve_ndk_path(self, ndk_path: Optional[str]) -> Optional[str]:
        """
        解析 NDK 路径

        优先级：
        1. 命令行传入的路径
        2. 环境变量 ANDROID_NDK_HOME
        3. 环境变量 ANDROID_NDK_ROOT
        4. 自动查找常见位置
        """
        # 1. 命令行参数
        if ndk_path:
            resolved = self._validate_ndk_path(ndk_path)
            if resolved:
                return resolved

        # 2. 环境变量 ANDROID_NDK_HOME
        env_ndk_home = os.environ.get("ANDROID_NDK_HOME")
        if env_ndk_home:
            resolved = self._validate_ndk_path(env_ndk_home)
            if resolved:
                print(f"Using NDK from ANDROID_NDK_HOME: {resolved}")
                return resolved

        # 3. 环境变量 ANDROID_NDK_ROOT
        env_ndk_root = os.environ.get("ANDROID_NDK_ROOT")
        if env_ndk_root:
            resolved = self._validate_ndk_path(env_ndk_root)
            if resolved:
                print(f"Using NDK from ANDROID_NDK_ROOT: {resolved}")
                return resolved

        # 4. 自动查找
        auto_path = self._find_ndk_auto()
        if auto_path:
            print(f"Auto-detected NDK: {auto_path}")
            return auto_path

        return None

    def _validate_ndk_path(self, ndk_path: str) -> Optional[str]:
        """验证 NDK 路径是否有效"""
        if not ndk_path:
            return None

        # 转换为绝对路径
        ndk_path = os.path.expanduser(ndk_path)
        ndk_path = os.path.expandvars(ndk_path)

        # 检查路径是否存在
        if not os.path.exists(ndk_path):
            print(f"Warning: NDK path does not exist: {ndk_path}")
            return None

        # 检查是否是 NDK 目录（检查关键文件）
        required_files = [
            "build/cmake/android.toolchain.cmake",
            "toolchains/llvm/prebuilt",
        ]

        for req_file in required_files:
            check_path = os.path.join(ndk_path, req_file)
            if not os.path.exists(check_path):
                print(f"Warning: NDK path appears invalid, missing: {req_file}")
                return None

        return os.path.abspath(ndk_path)

    def _find_ndk_auto(self) -> Optional[str]:
        """自动查找 NDK 安装位置"""
        possible_paths = []

        # 根据操作系统添加可能的路径
        if self.is_windows:
            possible_paths.extend([
                os.path.expandvars(r"%LOCALAPPDATA%\Android\Sdk\ndk"),
                os.path.expandvars(r"%USERPROFILE%\AppData\Local\Android\Sdk\ndk"),
                os.path.expandvars(r"C:\Android\android-sdk\ndk"),
                os.path.expandvars(r"C:\Program Files\Android\Sdk\ndk"),
                r"C:\android-ndk-*",
            ])
        else:  # Linux/macOS
            possible_paths.extend([
                os.path.expanduser("~/Android/Sdk/ndk"),
                os.path.expanduser("~/Library/Android/sdk/ndk"),
                "/opt/android-sdk/ndk",
                "/usr/local/android-sdk/ndk",
                "/usr/local/lib/android/sdk/ndk",
                "~/android-ndk-*",
            ])

        # 展开路径并查找
        expanded_paths = []
        for path_pattern in possible_paths:
            try:
                # 展开波浪号和通配符
                if '*' in path_pattern:
                    import glob
                    matches = glob.glob(os.path.expanduser(path_pattern))
                    expanded_paths.extend(matches)
                else:
                    expanded_path = os.path.expanduser(path_pattern)
                    if os.path.exists(expanded_path):
                        expanded_paths.append(expanded_path)
            except:
                continue

        # 查找每个路径下的最新版本
        for base_path in expanded_paths:
            if os.path.isdir(base_path):
                try:
                    # 获取所有子目录（假设是版本号）
                    subdirs = [d for d in os.listdir(base_path)
                             if os.path.isdir(os.path.join(base_path, d))]

                    if subdirs:
                        # 按版本号排序（简单字符串排序）
                        subdirs.sort(reverse=True)

                        # 验证每个目录，返回第一个有效的
                        for subdir in subdirs:
                            ndk_candidate = os.path.join(base_path, subdir)
                            if self._validate_ndk_path(ndk_candidate):
                                return ndk_candidate
                except:
                    continue

        return None

    def print_header(self, text: str):
        """打印标题"""
        print(f"\n{self.BOLD}{self.CYAN}{'='*70}{self.RESET}")
        print(f"{self.BOLD}{self.CYAN}{text.center(70)}{self.RESET}")
        print(f"{self.BOLD}{self.CYAN}{'='*70}{self.RESET}")

    def print_section(self, text: str):
        """打印章节标题"""
        print(f"\n{self.BOLD}{self.BLUE}{text}{self.RESET}")
        print(f"{self.BLUE}{'-' * len(text)}{self.RESET}")

    def print_success(self, text: str):
        """打印成功信息"""
        print(f"{self.GREEN}✓ {text}{self.RESET}")

    def print_error(self, text: str):
        """打印错误信息"""
        print(f"{self.RED}✗ {text}{self.RESET}")

    def print_warning(self, text: str):
        """打印警告信息"""
        print(f"{self.YELLOW}! {text}{self.RESET}")

    def print_info(self, text: str):
        """打印普通信息"""
        print(f"{self.PURPLE}  {text}{self.RESET}")

    def clean_directories(self) -> bool:
        """清理所有构建目录"""
        self.print_header("Cleaning Directories")

        dirs_to_clean = [
            ("Build Directory", self.build_dir),
            ("JNI Libs Directory", self.jni_dir),
        ]

        all_success = True
        for name, dir_path in dirs_to_clean:
            if os.path.exists(dir_path):
                try:
                    shutil.rmtree(dir_path)
                    self.print_success(f"Cleaned {name}: {os.path.basename(dir_path)}")
                except Exception as e:
                    self.print_error(f"Failed to clean {name}: {e}")
                    all_success = False

        # 重新创建目录
        try:
            os.makedirs(self.build_dir, exist_ok=True)
            os.makedirs(self.jni_dir, exist_ok=True)
            self.print_success("Created fresh directories")
        except Exception as e:
            self.print_error(f"Failed to create directories: {e}")
            all_success = False

        return all_success

    def check_dependencies(self) -> Tuple[bool, Dict]:
        """
        检查所有依赖

        Returns:
            Tuple[是否成功, 依赖信息字典]
        """
        self.print_section("Checking Dependencies")

        dependencies = {
            "ndk": {"found": False, "path": None, "version": None},
            "cmake": {"found": False, "version": None},
            "ninja": {"found": False, "version": None},
        }

        all_ok = True

        # 检查 NDK
        if not self.ndk_path:
            self.print_error("Android NDK not found!")
            self.print_info("Please specify NDK path using --ndk option")
            self.print_info("or set ANDROID_NDK_HOME environment variable")
            all_ok = False
        else:
            dependencies["ndk"]["found"] = True
            dependencies["ndk"]["path"] = self.ndk_path

            # 获取 NDK 版本
            try:
                source_properties = os.path.join(self.ndk_path, "source.properties")
                if os.path.exists(source_properties):
                    with open(source_properties, 'r') as f:
                        for line in f:
                            if line.startswith("Pkg.Revision"):
                                version = line.split('=')[1].strip()
                                dependencies["ndk"]["version"] = version
                                break
            except:
                pass

            if dependencies["ndk"]["version"]:
                self.print_success(f"NDK: {dependencies['ndk']['version']} at {self.ndk_path}")
            else:
                self.print_success(f"NDK found at: {self.ndk_path}")

        # 检查 CMakeLists.txt
        cmake_file = os.path.join(self.project_dir, "CMakeLists.txt")
        if not os.path.exists(cmake_file):
            self.print_error("CMakeLists.txt not found in project directory")
            all_ok = False
        else:
            self.print_success(f"Project: {os.path.basename(self.project_dir)}")

        # 检查 CMake
        try:
            result = subprocess.run(
                ["cmake", "--version"],
                capture_output=True,
                text=True,
                check=True
            )
            cmake_version = result.stdout.splitlines()[0]
            dependencies["cmake"]["found"] = True
            dependencies["cmake"]["version"] = cmake_version
            self.print_success(f"CMake: {cmake_version}")
        except (subprocess.CalledProcessError, FileNotFoundError):
            self.print_error("CMake not found in PATH")
            self.print_info("Install CMake from: https://cmake.org/download/")
            all_ok = False

        # 检查 Ninja
        try:
            result = subprocess.run(
                [self.make_program, "--version"],
                capture_output=True,
                text=True,
                check=True
            )
            ninja_version = result.stdout.strip()
            dependencies["ninja"]["found"] = True
            dependencies["ninja"]["version"] = ninja_version
            self.print_success(f"Ninja: {ninja_version}")
        except (subprocess.CalledProcessError, FileNotFoundError):
            self.print_warning("Ninja not found, falling back to make")
            self.generator = "Unix Makefiles" if not self.is_windows else "MinGW Makefiles"
            self.make_program = "make"

        return all_ok, dependencies

    def get_available_abis(self) -> List[Dict]:
        """获取可用的 ABI 列表"""
        return self.default_abis

    def build_for_abi(self, abi_info: Dict, cmake_flags: List[str] = None) -> Tuple[bool, List[str]]:
        """
        为特定 ABI 构建

        Args:
            abi_info: ABI 配置字典
            cmake_flags: 额外的 CMake 标志

        Returns:
            Tuple[是否成功, 生成的库文件列表]
        """
        abi_name = abi_info["name"]
        api_level = abi_info["api"]
        description = abi_info.get("description", "")

        print(f"\n{self.BOLD}Building for {abi_name}{self.RESET}")
        if description:
            print(f"{self.PURPLE}  {description} (API {api_level}){self.RESET}")
        print(f"{self.BLUE}{'─' * 50}{self.RESET}")

        # 创建目录
        abi_build_dir = os.path.join(self.build_dir, abi_name)
        abi_output_dir = os.path.join(self.jni_dir, abi_name)

        os.makedirs(abi_build_dir, exist_ok=True)
        os.makedirs(abi_output_dir, exist_ok=True)

        # 构建 CMake 命令
        cmake_cmd = [
            "cmake",
            self.project_dir,
            "-G", self.generator,
            f"-DCMAKE_TOOLCHAIN_FILE={os.path.join(self.ndk_path, 'build/cmake/android.toolchain.cmake')}",
            f"-DANDROID_ABI={abi_name}",
            f"-DANDROID_PLATFORM=android-{api_level}",
            "-DCMAKE_BUILD_TYPE=Release",
            f"-DCMAKE_LIBRARY_OUTPUT_DIRECTORY={abi_output_dir}",
            "-DCMAKE_CXX_FLAGS=-std=c++11 -Wall",
        ]

        # 添加额外的 CMake 标志
        if cmake_flags:
            cmake_cmd.extend(cmake_flags)

        # Windows 特定设置
        if self.is_windows and self.generator == "Ninja":
            ninja_path = os.path.join(self.ndk_path, "prebuilt", "windows-x86_64", "bin", "ninja.exe")
            if os.path.exists(ninja_path):
                cmake_cmd.extend(["-DCMAKE_MAKE_PROGRAM=" + ninja_path])

        # 执行 CMake 配置
        self.print_info("Configuring CMake...")
        try:
            result = subprocess.run(
                cmake_cmd,
                cwd=abi_build_dir,
                capture_output=True,
                text=True,
                check=True,
                encoding='utf-8'
            )
        except subprocess.CalledProcessError as e:
            self.print_error(f"CMake configuration failed for {abi_name}")
            if e.stderr:
                error_lines = e.stderr.strip().split('\n')
                for line in error_lines[-10:]:  # 显示最后10行错误
                    print(f"  {self.RED}{line}{self.RESET}")
            return False, []

        # 执行构建
        self.print_info("Building library...")
        try:
            result = subprocess.run(
                [self.make_program],
                cwd=abi_build_dir,
                capture_output=True,
                text=True,
                check=True,
                encoding='utf-8'
            )
        except subprocess.CalledProcessError as e:
            self.print_error(f"Build failed for {abi_name}")
            if e.stderr:
                error_lines = e.stderr.strip().split('\n')
                for line in error_lines[-10:]:
                    print(f"  {self.RED}{line}{self.RESET}")
            return False, []

        # 查找生成的库文件
        generated_libs = []

        # 在构建目录中查找
        for root, dirs, files in os.walk(abi_build_dir):
            for file in files:
                if file.endswith(".so"):
                    lib_path = os.path.join(root, file)
                    generated_libs.append(lib_path)

        # 在输出目录中查找
        for file in os.listdir(abi_output_dir):
            if file.endswith(".so"):
                lib_path = os.path.join(abi_output_dir, file)
                if lib_path not in generated_libs:
                    generated_libs.append(lib_path)

        # 报告结果
        if generated_libs:
            self.print_success(f"Successfully built {len(generated_libs)} libraries")
            for lib_path in generated_libs:
                lib_name = os.path.basename(lib_path)
                lib_size = os.path.getsize(lib_path)
                print(f"  {self.GREEN}→{self.RESET} {lib_name} ({lib_size:,} bytes)")
            return True, generated_libs
        else:
            self.print_warning(f"No .so files generated for {abi_name}")
            return True, []  # 仍然返回成功，因为构建过程没有错误

    def build_all(self, abis: List[Dict] = None, cmake_flags: List[str] = None) -> bool:
        """
        构建所有指定的 ABI

        Args:
            abis: 要构建的 ABI 列表，如果为 None 则使用默认列表
            cmake_flags: 额外的 CMake 标志

        Returns:
            是否成功
        """
        self.print_header("Android Native Library Builder")

        print(f"{self.BOLD}Project:{self.RESET} {self.project_dir}")
        print(f"{self.BOLD}NDK Path:{self.RESET} {self.ndk_path}")
        print(f"{self.BOLD}Platform:{self.RESET} {platform.system()}")

        # 检查依赖
        deps_ok, deps_info = self.check_dependencies()
        if not deps_ok:
            return False

        # 清理目录
        if not self.clean_directories():
            self.print_warning("Clean failed, but continuing...")

        # 确定要构建的 ABI
        if abis is None:
            abis = self.default_abis

        self.print_section("Building Libraries")
        print(f"Target ABIs: {', '.join(abi['name'] for abi in abis)}")

        # 构建每个 ABI
        success_count = 0
        failed_abis = []
        all_generated_libs = []

        for abi_info in abis:
            success, generated_libs = self.build_for_abi(abi_info, cmake_flags)
            if success:
                success_count += 1
                all_generated_libs.extend(generated_libs)
            else:
                failed_abis.append(abi_info["name"])

        # 生成报告
        self.print_header("Build Report")

        total_abis = len(abis)
        print(f"{self.BOLD}Total ABIs:{self.RESET} {total_abis}")
        print(f"{self.BOLD}Successful:{self.RESET} {success_count}")

        if failed_abis:
            print(f"{self.BOLD}Failed:{self.RESET} {len(failed_abis)}")
            for abi in failed_abis:
                print(f"  {self.RED}✗ {abi}{self.RESET}")

        # 显示输出目录结构
        if all_generated_libs:
            self.print_section("Generated Files")
            print(f"{self.BOLD}Output directory:{self.RESET} {self.jni_dir}")

            if os.path.exists(self.jni_dir):
                self._print_directory_structure(self.jni_dir, prefix="  ")

        # 显示下一步操作
        if success_count > 0:
            self.print_section("Next Steps")
            print("1. Copy jniLibs folder to your Android project:")
            print(f"   {self.BOLD}src/main/jniLibs/{self.RESET}")
            print()
            print("2. Add to your app's build.gradle:")
            print("   android {")
            print("       sourceSets {")
            print("           main {")
            print("               jniLibs.srcDirs = ['src/main/jniLibs']")
            print("           }")
            print("       }")
            print("   }")
            print()
            print("3. Or use in your app's CMakeLists.txt:")
            print("   add_library(your_lib SHARED IMPORTED)")
            print("   set_target_properties(your_lib PROPERTIES")
            print(f'       IMPORTED_LOCATION "${{CMAKE_CURRENT_SOURCE_DIR}}/../jniLibs/${{ANDROID_ABI}}/libdemo.so"')
            print("   )")

            return True
        else:
            self.print_error("No libraries were built successfully")
            return False

    def _print_directory_structure(self, directory: str, prefix: str = ""):
        """打印目录结构"""
        try:
            items = sorted(os.listdir(directory))
            for i, item in enumerate(items):
                item_path = os.path.join(directory, item)
                is_last = (i == len(items) - 1)

                if os.path.isdir(item_path):
                    print(f"{prefix}{'└── ' if is_last else '├── '}{self.BOLD}{item}/{self.RESET}")
                    new_prefix = prefix + ("    " if is_last else "│   ")
                    self._print_directory_structure(item_path, new_prefix)
                else:
                    size = os.path.getsize(item_path)
                    size_str = f"({size:,} bytes)" if size > 0 else ""
                    print(f"{prefix}{'└── ' if is_last else '├── '}{item} {size_str}")
        except:
            print(f"{prefix}[Error reading directory]")

    def list_abis(self):
        """列出所有支持的 ABI"""
        self.print_header("Available ABIs")

        abis = self.get_available_abis()
        for abi in abis:
            print(f"{self.BOLD}{abi['name']:15}{self.RESET} API {abi['api']:2}  {abi.get('description', '')}")


def main():
    parser = argparse.ArgumentParser(
        description="Build Android native libraries (.so files) for multiple ABIs",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s --ndk "C:\\Android\\ndk\\25.1.8937393"
  %(prog)s --abi arm64-v8a --ndk ~/Android/Sdk/ndk/25.1.8937393
  %(prog)s --clean
  %(prog)s --list-abis
  %(prog)s --cmake-flag "-DDEBUG=ON" --cmake-flag "-DOPTIMIZE=OFF"

Environment Variables:
  ANDROID_NDK_HOME    Path to Android NDK
  ANDROID_NDK_ROOT    Alternative environment variable for NDK path
        """
    )

    # NDK 路径参数
    ndk_group = parser.add_argument_group("NDK Configuration")
    ndk_group.add_argument(
        "--ndk",
        type=str,
        help="Path to Android NDK (required if not set in ANDROID_NDK_HOME)"
    )
    ndk_group.add_argument(
        "--ndk-version",
        type=str,
        help="NDK version to use (e.g., '25.1.8937393'), auto-detects latest if not specified"
    )

    # 构建目标参数
    build_group = parser.add_argument_group("Build Targets")
    build_group.add_argument(
        "--abi",
        type=str,
        help="Build specific ABI (e.g., 'arm64-v8a', 'armeabi-v7a')"
    )
    build_group.add_argument(
        "--api",
        type=int,
        help="Override Android API level for all ABIs"
    )
    build_group.add_argument(
        "--all-abis",
        action="store_true",
        default=True,
        help="Build all ABIs (default)"
    )
    build_group.add_argument(
        "--exclude",
        type=str,
        nargs="+",
        help="Exclude specific ABIs (e.g., --exclude x86 x86_64)"
    )

    # 构建选项
    option_group = parser.add_argument_group("Build Options")
    option_group.add_argument(
        "--build-type",
        type=str,
        choices=["Release", "Debug", "RelWithDebInfo", "MinSizeRel"],
        default="Release",
        help="CMake build type"
    )
    option_group.add_argument(
        "--cmake-flag",
        action="append",
        dest="cmake_flags",
        default=[],
        help="Additional CMake flags (can be used multiple times)"
    )
    option_group.add_argument(
        "--project-dir",
        type=str,
        help="Project directory (default: current directory)"
    )

    # 其他操作
    action_group = parser.add_argument_group("Actions")
    action_group.add_argument(
        "--clean",
        action="store_true",
        help="Clean build directories only"
    )
    action_group.add_argument(
        "--list-abis",
        action="store_true",
        help="List all available ABIs and exit"
    )
    action_group.add_argument(
        "--dry-run",
        action="store_true",
        help="Show what would be built without actually building"
    )
    action_group.add_argument(
        "--verbose",
        "-v",
        action="count",
        default=0,
        help="Increase verbosity level"
    )
    action_group.add_argument(
        "--no-color",
        action="store_true",
        help="Disable colored output"
    )

    args = parser.parse_args()

    # 创建构建器
    builder = AndroidLibraryBuilder(
        ndk_path=args.ndk,
        project_dir=args.project_dir
    )

    # 禁用颜色
    if args.no_color:
        builder.use_colors = False
        builder._init_colors()

    # 列出 ABI
    if args.list_abis:
        builder.list_abis()
        sys.exit(0)

    # 清理操作
    if args.clean:
        builder.clean_directories()
        sys.exit(0)

    # 准备构建配置
    abis_to_build = None

    if args.abi:
        # 构建特定 ABI
        all_abis = builder.get_available_abis()
        selected_abi = next((a for a in all_abis if a["name"] == args.abi), None)

        if not selected_abi:
            print(f"Error: Unknown ABI '{args.abi}'")
            print("Available ABIs:")
            for abi in all_abis:
                print(f"  {abi['name']}")
            sys.exit(1)

        if args.api:
            selected_abi["api"] = args.api

        abis_to_build = [selected_abi]
    else:
        # 构建多个 ABI
        all_abis = builder.get_available_abis()

        if args.exclude:
            abis_to_build = [abi for abi in all_abis if abi["name"] not in args.exclude]
        else:
            abis_to_build = all_abis.copy()

        # 覆盖 API 级别
        if args.api:
            for abi in abis_to_build:
                abi["api"] = args.api

    # 准备 CMake 标志
    cmake_flags = args.cmake_flags.copy()
    cmake_flags.append(f"-DCMAKE_BUILD_TYPE={args.build_type}")

    # 干运行
    if args.dry_run:
        print("\n" + "="*70)
        print("DRY RUN - No actual building will be performed")
        print("="*70)
        print(f"Project Directory: {builder.project_dir}")
        print(f"NDK Path: {builder.ndk_path}")
        print(f"Build Type: {args.build_type}")
        print(f"ABIs to build: {', '.join(abi['name'] for abi in abis_to_build)}")
        print(f"CMake Flags: {' '.join(cmake_flags)}")
        print(f"Output Directory: {builder.jni_dir}")
        sys.exit(0)

    # 执行构建
    success = builder.build_all(abis=abis_to_build, cmake_flags=cmake_flags)

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\nBuild interrupted by user")
        sys.exit(130)
    except Exception as e:
        print(f"\nUnexpected error: {e}")
        if os.environ.get("DEBUG"):
            import traceback
            traceback.print_exc()
        sys.exit(1)