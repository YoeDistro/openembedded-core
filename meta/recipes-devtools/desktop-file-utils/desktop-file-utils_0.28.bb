SUMMARY = "Command line utilities for working with *.desktop files"
DESCRIPTION = "desktop-file-utils contains a few command line utilities for working with desktop entries"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/validator.c;beginline=4;endline=27;md5=281e1114ee6c486a1a0a4295986b9416"

SRC_URI = "http://www.freedesktop.org/software/${BPN}/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "4401d4e231d842c2de8242395a74a395ca468cd96f5f610d822df33594898a70"

DEPENDS = "glib-2.0"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
        rm -rf ${D}${datadir}/emacs
}
