import 'dart:collection';

import 'package:flutter/material.dart';
import 'dart:io';

import 'package:media_gallery/media_gallery.dart';
import 'package:permission_handler/permission_handler.dart';

// void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Map<dynamic, dynamic> allImageInfo = new HashMap();
  List<String> allImage = [];
  List<String> allNameList = [];

  void requestStoragePermission() async {
    var status = await Permission.storage.status;
    print(status.isDenied);
    if (status.isDenied) {
      if (await Permission.storage.request().isGranted) {
        loadImages();
      }
    } else {
      loadImages();
    }
  }

  void loadImages() async {
    final List<MediaCollection> collections =
        await MediaGallery.listMediaCollections(
      mediaTypes: [MediaType.image, MediaType.video],
    );
    MediaCollection collection;
    for (var i = 0; i < collections.length; i++) {
      collection = collections[i];
      print("i = $i, item=${collection.name}");
      if (collection.name == "panoramas") {
        print(collection.name);
        print(collection.count);
        print(collection.id);
        _loadPanoramas(collection);
        break;
      }
    }
  }

  void _loadPanoramas(MediaCollection collection) async {
    var page = await collection.getMedias(
      mediaType: MediaType.image,
      take: collection.count,
    );
    List<String> pics = [];
    List<String> names = [];
    for (var i = 0; i < page.items.length; i++) {
      var media = page.items[i];
      var file = await media.getFile();
      var ff = media.creationDate.year;
      print(file.path + " " + ff.toString());
      pics.add(file.path);
      names.add(ff.toString());
    }
    setState(() {
      this.allImage = pics;
      this.allNameList = names;
    });
  }

  @override
  void initState() {
    super.initState();
    requestStoragePermission();
  }

  @override
  Widget build(BuildContext context) {



    return new MaterialApp(
      debugShowCheckedModeBanner: false,
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Image Gallery'),
        ),
        body: _buildGrid(),
      ),
    );
  }

  Widget _buildGrid() {
    return GridView.extent(
        maxCrossAxisExtent: 150.0,
        // padding: const EdgeInsets.all(4.0),
        mainAxisSpacing: 4.0,
        crossAxisSpacing: 4.0,
        children: _buildGridTileList(allImage.length));
  }

  List<Container> _buildGridTileList(int count) {
    return List<Container>.generate(
        count,
        (int index) => Container(
                child: new Column(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Image.file(
                  File(allImage[index].toString()),
                  width: 96.0,
                  height: 96.0,
                  fit: BoxFit.contain,
                ),
                Text(allNameList[index])
              ],
            )));
  }
}
