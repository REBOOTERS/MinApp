rm -rf build && mkdir build && cd build
cmake ..                      # 不传 Android toolchain，生成主机可执行文件
cmake --build . --config Release -j$(sysctl -n hw.ncpu)