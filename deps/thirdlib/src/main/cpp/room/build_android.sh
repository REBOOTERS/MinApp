#!/usr/bin/env bash
# build_android.sh - POSIX equivalent of build_android.bat
# Usage: ./build_android.sh --ndk /path/to/ndk

set -u
set -o pipefail

print_usage() {
  cat <<EOF
Usage: $0 [--ndk NDK_PATH] [--abis "abi1 abi2"]

Options:
  --ndk NDK_PATH     Path to Android NDK (required unless ANDROID_NDK_ROOT/ANDROID_NDK_HOME is set)
  --abis "..."       Space-separated ABIs to build for (default: "x86_64 arm64-v8a")
  --help             Show this help message

Example:
  $0 --ndk /path/to/android-ndk --abis "arm64-v8a x86_64"
EOF
}

# Default ABIs
DEFAULT_ABIS=("x86_64" "arm64-v8a")
ABIS=()
NDK_PATH=""

# parse args
POSITIONAL=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --ndk)
      NDK_PATH="$2"
      shift 2
      ;;
    --abis)
      shift
      IFS=' ' read -r -a ABIS <<< "$1"
      shift
      ;;
    --help|-h)
      print_usage
      exit 0
      ;;
    --*)
      echo "Unknown option: $1"
      print_usage
      exit 1
      ;;
    *)
      POSITIONAL+=("$1")
      shift
      ;;
  esac
done

# If NDK not set via arg, try env vars
if [[ -z "$NDK_PATH" ]]; then
  if [[ -n "${ANDROID_NDK_HOME-}" ]]; then
    NDK_PATH="$ANDROID_NDK_HOME"
  elif [[ -n "${ANDROID_NDK_ROOT-}" ]]; then
    NDK_PATH="$ANDROID_NDK_ROOT"
  fi
fi

if [[ -z "$NDK_PATH" ]]; then
  echo "Error: NDK path not provided. Use --ndk or set ANDROID_NDK_HOME/ANDROID_NDK_ROOT."
  print_usage
  exit 2
fi

# Normalize script/project paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
BUILD_DIR="$PROJECT_DIR/build_android"
JNI_LIBS_DIR="$PROJECT_DIR/jniLibs"

if [[ ${#ABIS[@]} -eq 0 ]]; then
  ABIS=("${DEFAULT_ABIS[@]}")
fi

echo "PROJECT_DIR = $PROJECT_DIR"
echo "BUILD_DIR = $BUILD_DIR"

echo "Cleaning build and jniLibs directories..."
rm -rf "$BUILD_DIR"
rm -rf "$JNI_LIBS_DIR"
mkdir -p "$BUILD_DIR"

for ABI in "${ABIS[@]}"; do
  echo
  echo "Building for $ABI..."
  mkdir -p "$BUILD_DIR/$ABI"
  pushd "$BUILD_DIR/$ABI" > /dev/null || { echo "Failed to enter build dir"; continue; }

  cmake "$PROJECT_DIR" \
    -G "Ninja" \
    -DCMAKE_TOOLCHAIN_FILE="$NDK_PATH/build/cmake/android.toolchain.cmake" \
    -DANDROID_ABI="$ABI" \
    -DANDROID_PLATFORM=android-21 \
    -DCMAKE_BUILD_TYPE=Release

  if [[ $? -eq 0 ]]; then
    cmake --build .
    if [[ $? -ne 0 ]]; then
      echo "Failed to build for $ABI"
      popd > /dev/null
      continue
    fi

    echo "Successfully built for $ABI"

    # Copy .so files to jniLibs
    echo "Copying .so files to jniLibs..."
    TARGET_DIR="$JNI_LIBS_DIR/$ABI"
    mkdir -p "$TARGET_DIR"

    # Find and copy .so files from the build tree
    COPIED=0
    while IFS= read -r -d '' sofile; do
      echo "Copying $sofile to $TARGET_DIR"
      cp -f "$sofile" "$TARGET_DIR/"
      COPIED=$((COPIED+1))
    done < <(find . -type f -name "*.so" -print0 2>/dev/null)

    # If none found, try top-level of this build dir
    if [[ $COPIED -eq 0 ]]; then
      for f in *.so; do
        if [[ -f "$f" ]]; then
          echo "Copying $f to $TARGET_DIR"
          cp -f "$f" "$TARGET_DIR/"
          COPIED=$((COPIED+1))
        fi
      done
    fi

    if [[ $COPIED -gt 0 ]]; then
      echo "Successfully copied $COPIED .so file(s) for $ABI"
    else
      echo "Warning: No .so files found for $ABI"
    fi

  else
    echo "Failed to configure for $ABI"
  fi

  popd > /dev/null || true
done

# Summary
echo
echo "========================================"
echo "Build Summary:"
echo "========================================"
echo "Build directory: $BUILD_DIR"
echo "jniLibs directory: $JNI_LIBS_DIR"
for ABI in "${ABIS[@]}"; do
  TARGET_DIR="$JNI_LIBS_DIR/$ABI"
  if [[ -d "$TARGET_DIR" ]]; then
    COUNT=$(find "$TARGET_DIR" -maxdepth 1 -type f -name "*.so" 2>/dev/null | wc -l | tr -d ' ')
    echo "$ABI: $COUNT .so file(s)"
  else
    echo "$ABI: 0 .so file(s)"
  fi
done
echo "========================================"

echo "Build completed!"

exit 0
