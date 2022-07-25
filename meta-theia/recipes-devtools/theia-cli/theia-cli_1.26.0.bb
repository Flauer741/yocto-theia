# SPDX-License-Identifier: MIT

# TODO: generate list of theia licenses (including dependencies)
LICENSE = "CLOSED"

SRC_URI += " \
    file://package.json;subdir=yarn \
    file://yarn.lock;subdir=yarn \
"

YARNLOCK = "${THISDIR}/${BPN}/yarn.lock"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
S = "${WORKDIR}/yarn"

inherit yarn

do_compile() {
    echo 'yarn-offline-mirror "../offline_packages"' > .yarnrc
    ${YARN} --production
    ${YARN} autoclean --init
    echo *.ts >> .yarnclean
    echo *.ts.map >> .yarnclean
    echo *.spec.* >> .yarnclean
    ${YARN} autoclean --force
}

FILES:${PN} = " \
    ${libdir}/node_modules \
    ${bindir}/theia \
"

# TODO: try to use 'yarn pack' for packaging/installing
do_install() {
    install -d ${D}${libdir} ${D}${bindir}
    cp -RP --preserve=mode,links node_modules ${D}${libdir}/
    ln -rs ${D}${libdir}/node_modules/.bin/theia ${D}${bindir}/theia
}

# TODO: we are probably installing a lof of stuff we do not need, including static libs...
# plugins contains binaries for from architectures
# plugins have a lot of file-rdeps QA issues
#INSANE_SKIP:${PN} += "staticdev file-rdeps arch"

BBCLASSEXTEND="native"
