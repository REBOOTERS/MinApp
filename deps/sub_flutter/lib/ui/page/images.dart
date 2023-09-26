import 'dart:collection';

import 'package:flutter/material.dart';
import 'dart:io';

import 'package:media_gallery/media_gallery.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:sub_flutter/ui/page/picture_gallery.dart';

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
      if (collection.name == "panoramas" || collection.name == "All") {
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
    return new Scaffold(
      appBar: new AppBar(
        title: const Text('Panorama Gallery'),
      ),
      body: _buildGrid(),
    );
  }

  Widget _buildGrid() {
    return GridView.count(
      crossAxisCount: 3,
      padding: EdgeInsets.symmetric(vertical: 0),
      children: _buildGridTileList(allImage.length),
    );
  }

  List<Container> _buildGridTileList(int count) {
    final screenWidth = MediaQuery.of(context).size.width;

    return List<Container>.generate(
        count,
        (int index) => Container(
                child: new Column(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                GestureDetector(
                  child: Image.file(
                    File(allImage[index].toString()),
                    width: screenWidth / 3,
                    height: screenWidth / 3,
                    fit: BoxFit.cover,
                  ),
                  onTap: () {
                    Navigator.of(context).push(new FadeRoute(
                        page: PictureGalleryScreen(
                      images: allImage, //传入图片list
                      index: index, //传入当前点击的图片的index
                      heroTag: "img", //传入当前点击的图片的hero tag （可选）
                    )));
                  },
                ),
                // Text(allNameList[index])
              ],
            )));
  }
}

class FadeRoute extends PageRouteBuilder {
  final Widget page;

  FadeRoute({this.page})
      : super(
          pageBuilder: (
            BuildContext context,
            Animation<double> animation,
            Animation<double> secondaryAnimation,
          ) =>
              page,
          transitionsBuilder: (
            BuildContext context,
            Animation<double> animation,
            Animation<double> secondaryAnimation,
            Widget child,
          ) =>
              FadeTransition(
            opacity: animation,
            child: child,
          ),
        );
}
