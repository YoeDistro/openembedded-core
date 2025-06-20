SUMMARY = "libc and patchelf tarball for use with uninative.bbclass"
LICENSE = "MIT"

TOOLCHAIN_TARGET_TASK = ""

TOOLCHAIN_HOST_TASK = "\
    nativesdk-glibc \
    nativesdk-glibc-dbg \
    nativesdk-glibc-gconvs \
    nativesdk-patchelf \
    nativesdk-libxcrypt \
    nativesdk-libxcrypt-compat \
    nativesdk-libnss-nis \
    nativesdk-sdk-provides-dummy \
    nativesdk-libgcc \
    "

INHIBIT_DEFAULT_DEPS = "1"

MULTIMACH_TARGET_SYS = "${SDK_ARCH}-nativesdk${SDK_VENDOR}-${SDK_OS}"
PACKAGE_ARCH = "${SDK_ARCH}_${SDK_OS}"
PACKAGE_ARCHS = ""
TARGET_ARCH = "none"
TARGET_OS = "none"

TOOLCHAIN_OUTPUTNAME ?= "${SDK_ARCH}-nativesdk-libc"

RDEPENDS = "${TOOLCHAIN_HOST_TASK}"

EXCLUDE_FROM_WORLD = "1"

inherit populate_sdk
inherit nopackages

deltask install
deltask populate_sysroot

do_populate_sdk[stamp-extra-info] = "${PACKAGE_ARCH}"

SDK_DEPENDS += "patchelf-native"

SDK_PACKAGING_FUNC = ""
REAL_MULTIMACH_TARGET_SYS = "none"

fakeroot create_sdk_files() {
	cp ${COREBASE}/scripts/relocate_sdk.py ${SDK_OUTPUT}/${SDKPATH}/

	# Replace the ##DEFAULT_INSTALL_DIR## with the correct pattern.
	# Escape special characters like '+' and '.' in the SDKPATH
	escaped_sdkpath=$(echo ${SDKPATH}/sysroots/${SDK_SYS} |sed -e "s:[\+\.]:\\\\\\\\\0:g")
	sed -i -e "s:##DEFAULT_INSTALL_DIR##:$escaped_sdkpath:" ${SDK_OUTPUT}/${SDKPATH}/relocate_sdk.py
}

fakeroot archive_sdk() {
	cd ${SDK_OUTPUT}/${SDKPATH}

	DEST="./${SDK_ARCH}-${SDK_OS}"
	mv sysroots/${SDK_SYS} $DEST
	rm sysroots -rf
	# There is a check in meta/files/toolchain-shar-extract.sh -- make sure to
	# keep that check up to date if changing the `1024`
	patchelf --set-interpreter ${@''.join('a' for n in range(1024))} $DEST/usr/bin/patchelf
	mv $DEST/usr/bin/patchelf $DEST/usr/bin/patchelf-uninative
	${SDK_ARCHIVE_CMD}
}
