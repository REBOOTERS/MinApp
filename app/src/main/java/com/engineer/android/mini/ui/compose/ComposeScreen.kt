package com.engineer.android.mini.ui.compose

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.engineer.android.mini.ui.compose.pickimg.TransViewModel

@Composable
fun ImagePickScreen(
    pickImage: () -> Unit, generateImage: () -> Unit, viewModel: TransViewModel
) {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val styleImages by viewModel.styleImages.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64FFDA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 两个 Image 平分屏幕高度（各占 50%）
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 选中的图片
                viewModel.pickedImageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Picked Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 生成的图片
                viewModel.transformedImageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Transformed Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            // 仅当 selectedOption 为 3 或 4 时显示风格列表
            if (selectedOption == 3 || selectedOption == 4) {
                StyleImageList(
                    images = styleImages, onStyleSelected = { index, bitmap ->
                        // 处理选中的风格索引（例如更新ViewModel）
                        viewModel.updateSelectedStyle(index)
                        viewModel.updateStyleImage(bitmap)
                        generateImage()
                    }, viewModel
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (0..4).forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = (option == selectedOption),
                            onClick = { viewModel.updateSelectedOption(option) })
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                viewModel.updateSelectedOption(option)
                                generateImage()
                            })
                        Text(text = option.toString(), modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            // 按钮固定在底部
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = pickImage, modifier = Modifier
                        .weight(1f)
                        .padding(end = 5.dp)
                ) {
                    Text("pick_img")
                }

                Spacer(modifier = Modifier.width(5.dp))

                Button(
                    onClick = generateImage, modifier = Modifier
                        .weight(1f)
                        .padding(start = 5.dp)
                ) {
                    Text("gan_gen")
                }
            }
        }

        // Loading indicator
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun StyleImageList(
    images: List<ImageBitmap>, onStyleSelected: (Int, Bitmap) -> Unit, viewModel: TransViewModel
) {
//    var selectedIndex by remember { mutableStateOf(-1) } // 当前选中项
    val selectedIndex by viewModel.selectedStyleIndex.collectAsState()
    Column {
        Text("选择风格:", modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(4.dp)
        ) {
            images.forEachIndexed { index, bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Style Image",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp)
                        // 根据选中状态添加边框
                        .border(
                            border = if (selectedIndex == index) BorderStroke(1.dp, Color.Red)
                            else BorderStroke(0.dp, Color.Transparent),
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable {
                            onStyleSelected(index, bitmap.asAndroidBitmap()) // 通知外部选中变化
                        })
            }
        }
    }
}