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

python () {
    import re
    import base64

    ylock_re = re.compile(r'^"?(?P<name>@?[^ #\n][^@]*).*:\n  version "(?P<version>[^"]+)"\n  resolved "(?P<url>[^"]+)"\n  integrity (?P<algo>[^-]+)-(?P<hash>.*)$', re.MULTILINE)

    with open(d.getVar('YARNLOCK'), 'r') as yarn:
        for m in ylock_re.finditer(yarn.read()):
            fullname = m.group("name").replace("/", "-") + "-" + m.group("version")
            fullname_noat = fullname.replace("@", "at-")
            hashalgo = m.group("algo") + "sum"
            hashsum = base64.b64decode(m.group("hash")).hex()

            d.appendVar("SRC_URI", f' {m.group("url")};name={fullname_noat};downloadfilename={fullname}.tgz;subdir=offline_packages;unpack=0')
            d.setVarFlag("SRC_URI", f'{fullname_noat}.{hashalgo}', hashsum)
}

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
DEPENDS = "yarn-native pkgconfig-native libsecret"
RDEPENDS:${PN} = "nodejs bash libstdc++ glibc libsecret glib-2.0 libgcc"

S = "${WORKDIR}/yarn"

do_compile() {
    echo 'yarn-offline-mirror "../offline_packages"' > .yarnrc
    YARN="yarn --no-default-rc --use-yarnrc=.yarnrc --offline --pure-lockfile"
    $YARN
    $YARN theia build
    $YARN --production
    $YARN autoclean --init
    echo *.ts >> .yarnclean
    echo *.ts.map >> .yarnclean
    echo *.spec.* >> .yarnclean
    $YARN autoclean --force
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
INSANE_SKIP:${PN} += "staticdev file-rdeps arch"
