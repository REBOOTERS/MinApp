import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';
import 'package:photo_view/photo_view_gallery.dart';
import 'dart:io';

class PictureGalleryScreen extends StatefulWidget {
  List images = [];
  int index = 0;
  String heroTag;
  PageController controller;

  PictureGalleryScreen(
      {Key key,
      @required this.images,
      this.index,
      this.controller,
      this.heroTag})
      : super(key: key) {
    controller = PageController(initialPage: index);
  }

  @override
  _PhotoViewGalleryScreenState createState() => _PhotoViewGalleryScreenState();
}

class _PhotoViewGalleryScreenState extends State<PictureGalleryScreen> {
  int currentIndex = 0;

  @override
  void initState() {
    super.initState();
    currentIndex = widget.index;
  }

  void onPageChanged(int index) {
    setState(() {
      currentIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    new Material(
        child: Container(
            child: Stack(
      alignment: Alignment.bottomRight,
      children: [
        PhotoViewGallery.builder(
          scrollPhysics: const BouncingScrollPhysics(),
          builder: (BuildContext context, int index) {
            return PhotoViewGalleryPageOptions(
              imageProvider: FileImage(File(widget.images[index])),
              initialScale: PhotoViewComputedScale.covered * 1,
              heroAttributes: PhotoViewHeroAttributes(tag: widget.heroTag),
            );
          },
          itemCount: widget.images.length,
          loadingBuilder: (context, event) => Center(
            child: Container(
              width: 20.0,
              height: 20.0,
              child: CircularProgressIndicator(
                value: event == null
                    ? 0
                    : event.cumulativeBytesLoaded / event.expectedTotalBytes,
              ),
            ),
          ),
          backgroundDecoration: null,
          pageController: widget.controller,
          onPageChanged: onPageChanged,
        ),
        (Container(
          padding: const EdgeInsets.all(20.0),
          child: Text(
            "${currentIndex + 1}/${widget.images.length}",
            style: const TextStyle(
              color: Colors.black,
              decoration: TextDecoration.none,
              fontSize: 17.0,
            ),
          ),
        ))
      ],
    )));
  }
}
