# SPDX-License-Identifier: MIT
FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SUMMARY = "Hardware database management tool from systemd"

require systemd.inc

DEPENDS = "gperf-native libcap-native util-linux-native python3-jinja2-native"

# TODO: Remove STATX_MNT_ID patch once minimum supported build host kernel is >= 5.8 (RHEL 8 EOL: 2029)
SRC_URI += "file://Handle-missing-pidfd_open-and-STATX_MNT_ID-on-older-.patch \
            file://hwdb-use-compat-mode-for-reproducible-cross-builds.patch \
           "

inherit pkgconfig meson native

MESON_TARGET = "systemd-hwdb"

# Override prefix so compiled-in UDEVLIBEXECDIR (/usr/lib/udev) matches the
# target rootfs layout. This allows --root $D --usr to find hwdb.d source
# files and write hwdb.bin to the correct location.
EXTRA_OEMESON += "--prefix /usr"
EXTRA_OEMESON += "-Dhwdb=true -Dlink-udev-shared=false"
EXTRA_OEMESON += "-Dpam=disabled -Daudit=disabled -Dselinux=disabled"
EXTRA_OEMESON += "-Dacl=disabled -Dapparmor=disabled -Dseccomp=disabled"
EXTRA_OEMESON += "-Dlibcryptsetup=disabled -Dlibcurl=disabled -Dlibfido2=disabled"
EXTRA_OEMESON += "-Dpcre2=disabled -Dp11kit=disabled -Dopenssl=disabled"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/systemd-hwdb ${D}${bindir}/systemd-hwdb
}
