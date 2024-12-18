SUMMARY = "A pure python RFC3339 validator"
HOMEPAGE = "https://github.com/naimetti/rfc3339-validator"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a21b13b5a996f08f7e0b088aa38ce9c6"

FILESEXTRAPATHS:prepend := "${THISDIR}/python-rfc3339-validator:"

SRC_URI[sha256sum] = "138a2abdf93304ad60530167e51d2dfb9549521a836871b88d7f4695d0022f6b"

PYPI_PACKAGE = "rfc3339_validator"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-core \
    python3-datetime \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"
