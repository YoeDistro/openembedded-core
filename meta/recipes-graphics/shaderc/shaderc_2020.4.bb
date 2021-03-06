SUMMARY  = "A collection of tools, libraries and tests for shader compilation"
DESCRIPTION = "The Shaderc library provides an API for compiling GLSL/HLSL \
source code to SPIRV modules. It has been shipping in the Android NDK since version r12b."
SECTION = "graphics"
HOMEPAGE = "https://github.com/google/shaderc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "0dbd899941a43ffd55df527d65128b3b66e75c9c"
SRC_URI = "git://github.com/google/shaderc.git;protocol=https;branch=main \
           file://0001-cmake-de-vendor-libs-and-disable-git-versioning.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"
S = "${WORKDIR}/git"

inherit cmake python3native

DEPENDS = "spirv-headers spirv-tools glslang"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DSHADERC_SKIP_TESTS=ON \
    -Dglslang_SOURCE_DIR=${STAGING_INCDIR}/glslang \
"

BBCLASSEXTEND = "native nativesdk"

# TODO: probably there is better solution for this.
# I don't know any method for get the version of a receipe in DEPENDS,
# so do this ugly hack
inherit pkgconfig
DEPENDS += "glslang-native"
do_configure_prepend() {
    cat <<- EOF > ${S}/glslc/src/build-version.inc
"${PV}\\n"
"$(pkg-config --modversion SPIRV-Tools)\\n"
"$(glslangValidator --version | head -1 | cut -d' ' -f3)\\n"
EOF
}
