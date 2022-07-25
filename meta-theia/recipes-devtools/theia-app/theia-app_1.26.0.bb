# SPDX-License-Identifier: MIT

# TODO: generate list of theia licenses (including dependencies)
LICENSE = "CLOSED"

inherit systemd

#require plugins.inc

SRC_URI += " \
    file://yarn.lock;subdir=yarn \
    file://package.json;subdir=yarn \
    file://theia-app.service \
"

YARNLOCK = "${THISDIR}/${PN}/yarn.lock"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
DEPENDS = "pkgconfig-native libsecret theia-cli-native"
RDEPENDS:${PN} = "nodejs bash libstdc++ glibc libsecret glib-2.0 libgcc"

S = "${WORKDIR}/yarn"

inherit yarn

do_compile[vardeps] += " npm_config_arch CC_host CXX_host"
do_compile() {
    echo 'yarn-offline-mirror "../offline_packages"' > .yarnrc
    ${YARN}
    theia build
    ${YARN} --production
    ${YARN} autoclean --init
    echo *.ts >> .yarnclean
    echo *.ts.map >> .yarnclean
    echo *.spec.* >> .yarnclean
    ${YARN} autoclean --force
}

TARGET_LOCATION = "/opt/theia-app"

FILES:${PN} = " \
    ${TARGET_LOCATION}/* \
    ${TARGET_LOCATION}/.* \
    ${systemd_system_unitdir}/theia-app.service \
"

SYSTEMD_SERVICE:${PN} += "theia-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# TODO: try to use 'yarn pack' for packaging/installing
do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/../theia-app.service ${D}${systemd_system_unitdir}

    install -d ${D}${TARGET_LOCATION}
    cp -RP --preserve=mode,links . ${D}${TARGET_LOCATION}/
}

# TODO: we are probably installing a lof of stuff we do not need, including static libs...
# plugins contains binaries for from architectures
# plugins have a lot of file-rdeps QA issues
INSANE_SKIP:${PN} += "staticdev file-rdeps arch already-stripped"
