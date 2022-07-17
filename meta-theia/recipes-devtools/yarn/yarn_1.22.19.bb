# SPDX-License-Identifier: MIT

SUMMARY = "📦🐈 Fast, reliable, and secure dependency management."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9afb19b302b259045f25e9bb91dd34d6"

SRC_URI = "https://github.com/yarnpkg/yarn/releases/download/v${PV}/${BPN}-v${PV}.tar.gz"
SRC_URI[sha256sum] = "732620bac8b1690d507274f025f3c6cfdc3627a84d9642e38a07452cc00e0f2e"

DEPENDS = "nodejs-native"
RDEPENDS:${PN} = "nodejs"

S = "${WORKDIR}/${BPN}-v${PV}"

do_install() {
    install -d ${D}${bindir} ${D}${libdir}
    install -m 0755 ./bin/yarn ./bin/yarn.js ./bin/yarnpkg ${D}${bindir}/
    install -m 0644 ./lib/cli.js ./lib/v8-compile-cache.js ${D}${libdir}/
}

LICENSE_${PN} = "BSD-2-Clause"
BBCLASSEXTEND = "native"
