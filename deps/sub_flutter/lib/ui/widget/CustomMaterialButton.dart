import 'package:flutter/material.dart';
import 'package:sub_flutter/ui/page/gallery.dart';
import 'package:sub_flutter/ui/page/images.dart';

class CustomMaterialButton extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    void _gotoPage() => {
          Navigator.of(context)
              .push(MaterialPageRoute(builder: (context) => MyApp()))
        };

    return MaterialButton(
        height: 40,
        elevation: 5,
        color: Colors.orangeAccent,
        textColor: Colors.white,
        splashColor: Colors.blue,
        padding: EdgeInsets.all(8),
        child: Text("MaterialButton"),
        onPressed: () => _gotoPage());
  }
}
