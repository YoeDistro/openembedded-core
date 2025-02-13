SUMMARY = "userspace utilities for kernel nfs"
DESCRIPTION = "The nfs-utils package provides a daemon for the kernel \
NFS server and related tools."
HOMEPAGE = "http://nfs.sourceforge.net/"
SECTION = "console/network"

LICENSE = "MIT & GPL-2.0-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=95f3a93a5c3c7888de623b46ea085a84"

# util-linux for libblkid
DEPENDS = "libcap libevent util-linux sqlite3 libtirpc libxml2"
RDEPENDS:${PN} = "${PN}-client"
RRECOMMENDS:${PN} = "kernel-module-nfsd"

inherit useradd

USERADD_PACKAGES = "${PN}-client"
USERADD_PARAM:${PN}-client = "--system  --home-dir /var/lib/nfs \
			      --shell /bin/false --user-group rpcuser"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/nfs-utils/${PV}/nfs-utils-${PV}.tar.xz \
           file://nfsserver \
           file://nfscommon \
           file://nfs-utils.conf \
           file://nfs-server.service \
           file://nfs-mountd.service \
           file://nfs-statd.service \
           file://proc-fs-nfsd.mount \
           file://nfs-utils-debianize-start-statd.patch \
           file://bugfix-adjust-statd-service-name.patch \
           file://0001-Makefile.am-fix-undefined-function-for-libnsm.a.patch \
           file://clang-warnings.patch \
           file://0001-locktest-Makefile.am-Do-not-use-build-flags.patch \
           file://0001-Fix-typecast-warning-with-clang.patch \
           "

SRC_URI[sha256sum] = "a39bbea76ac0ab9e6e8699caf3c308b6b310c20d458e8fa8606196d358e7fb15"

# Only kernel-module-nfsd is required here (but can be built-in)  - the nfsd module will
# pull in the remainder of the dependencies.

INITSCRIPT_PACKAGES = "${PN} ${PN}-client"
INITSCRIPT_NAME = "nfsserver"
INITSCRIPT_PARAMS = "defaults"
INITSCRIPT_NAME:${PN}-client = "nfscommon"
INITSCRIPT_PARAMS:${PN}-client = "defaults 19 21"

inherit autotools-brokensep update-rc.d systemd pkgconfig

SYSTEMD_PACKAGES = "${PN} ${PN}-client"
SYSTEMD_SERVICE:${PN} = "nfs-server.service nfs-mountd.service"
SYSTEMD_SERVICE:${PN}-client = "nfs-statd.service"

# --enable-uuid is need for cross-compiling
EXTRA_OECONF = "--with-statduser=rpcuser \
                --enable-mountconfig \
                --enable-libmount-mount \
                --enable-uuid \
                --disable-gss \
                --disable-nfsdcltrack \
                --with-statdpath=/var/lib/nfs/statd \
                --with-rpcgen=${HOSTTOOLS_DIR}/rpcgen \
               "

LDFLAGS += "-lsqlite3 -levent"

PACKAGECONFIG ??= "tcp-wrappers \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG:remove:libc-musl = "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers,tcp-wrappers"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
# libdevmapper is available in meta-oe
PACKAGECONFIG[nfsv41] = "--enable-nfsv41,--disable-nfsv41,libdevmapper,libdevmapper"
# keyutils is available in meta-oe
PACKAGECONFIG[nfsv4] = "--enable-nfsv4,--disable-nfsv4,keyutils,python3-core"
PACKAGECONFIG[nfsdctl] = "--enable-nfsdctl,--disable-nfsdctl,libnl readline,"

PACKAGES =+ "${PN}-client ${PN}-mount ${PN}-stats ${PN}-rpcctl"

CONFFILES:${PN}-client += "${localstatedir}/lib/nfs/etab \
			   ${localstatedir}/lib/nfs/rmtab \
			   ${localstatedir}/lib/nfs/xtab \
			   ${localstatedir}/lib/nfs/statd/state \
			   ${sysconfdir}/nfsmount.conf"

FILES:${PN}-client = "${sbindir}/*statd \
		      ${libdir}/libnfsidmap.so.* \
		      ${sbindir}/rpc.idmapd ${sbindir}/sm-notify \
		      ${sbindir}/showmount ${sbindir}/nfsstat \
		      ${localstatedir}/lib/nfs \
		      ${sysconfdir}/nfs-utils.conf \
		      ${sysconfdir}/nfsmount.conf \
		      ${sysconfdir}/init.d/nfscommon \
		      ${systemd_system_unitdir}/nfs-statd.service"
RDEPENDS:${PN}-client = "${PN}-mount rpcbind"

FILES:${PN}-mount = "${base_sbindir}/*mount.nfs*"

FILES:${PN}-stats = "${sbindir}/mountstats ${sbindir}/nfsiostat ${sbindir}/nfsdclnts"
RDEPENDS:${PN}-stats = "python3-core"

FILES:${PN}-rpcctl = "${sbindir}/rpcctl"
RDEPENDS:${PN}-rpcctl = "python3-core"

FILES:${PN}-staticdev += "${libdir}/libnfsidmap/*.a"

FILES:${PN} += "${systemd_unitdir} ${libdir}/libnfsidmap/ ${nonarch_libdir}/modprobe.d"

do_configure:prepend() {
	sed -i -e 's,sbindir = /sbin,sbindir = ${base_sbindir},g' \
		${S}/utils/mount/Makefile.am
}

# Make clean needed because the package comes with
# precompiled 64-bit objects that break the build
do_compile:prepend() {
	make clean
}

# Works on systemd only
HIGH_RLIMIT_NOFILE ??= "4096"

do_install:append () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${UNPACKDIR}/nfsserver ${D}${sysconfdir}/init.d/nfsserver
	install -m 0755 ${UNPACKDIR}/nfscommon ${D}${sysconfdir}/init.d/nfscommon

	install -m 0755 ${UNPACKDIR}/nfs-utils.conf ${D}${sysconfdir}
	install -m 0755 ${S}/utils/mount/nfsmount.conf ${D}${sysconfdir}

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${UNPACKDIR}/nfs-server.service ${D}${systemd_system_unitdir}/
	install -m 0644 ${UNPACKDIR}/nfs-mountd.service ${D}${systemd_system_unitdir}/
	install -m 0644 ${UNPACKDIR}/nfs-statd.service ${D}${systemd_system_unitdir}/
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@SYSCONFDIR@,${sysconfdir},g' \
		-e 's,@HIGH_RLIMIT_NOFILE@,${HIGH_RLIMIT_NOFILE},g' \
		${D}${systemd_system_unitdir}/*.service
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -m 0644 ${UNPACKDIR}/proc-fs-nfsd.mount ${D}${systemd_system_unitdir}/
		install -d ${D}${systemd_system_unitdir}/sysinit.target.wants/
		ln -sf ../proc-fs-nfsd.mount ${D}${systemd_system_unitdir}/sysinit.target.wants/proc-fs-nfsd.mount
	fi

	# kernel code as of 3.8 hard-codes this path as a default
	install -d ${D}/var/lib/nfs/v4recovery

	# chown the directories and files
	chown -R rpcuser:rpcuser ${D}${localstatedir}/lib/nfs/statd
	chmod 0644 ${D}${localstatedir}/lib/nfs/statd/state
}
