SUMMARY = "manage symlinks in /etc/rcN.d"
HOMEPAGE = "http://github.com/philb/update-rc.d/"
DESCRIPTION = "update-rc.d is a utility that allows the management of symlinks to the initscripts in the /etc/rcN.d directory structure."
SECTION = "base"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://update-rc.d;beginline=5;endline=15;md5=d40a07c27f535425934bb5001f2037d9"

SRC_URI = "git://git.yoctoproject.org/update-rc.d;branch=master;protocol=https"
SRCREV = "b8f950105010270a768aa12245d6abf166346015"
PV .= "+git"

UPSTREAM_CHECK_COMMITS = "1"

inherit allarch

do_compile() {
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/update-rc.d ${D}${sbindir}/update-rc.d
}

BBCLASSEXTEND = "native nativesdk"
