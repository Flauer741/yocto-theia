# SPDX-License-Identifier: MIT

SUMMARY = "📦🐈 Fast, reliable, and secure dependency management."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9afb19b302b259045f25e9bb91dd34d6"

SRC_URI = "npm://registry.npmjs.org/;package=yarn;version=${PV}"

S = "${WORKDIR}/npm"

inherit npm

LICENSE:${PN} = "BSD-2-Clause"
BBCLASSEXTEND = "native"

