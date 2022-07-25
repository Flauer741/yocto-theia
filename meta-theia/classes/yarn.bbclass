# PDX-License-Identifier: MIT

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

    build_arch = d.getVar('BUILD_ARCH')
    host_arch = d.getVar('HOST_ARCH')

    if build_arch != host_arch:
        arch_mapping = { "aarch64": "arm64", "x86_64": "x64" }
        d.setVar("npm_config_arch", arch_mapping.get(host_arch, host_arch))
        d.setVar("CC_host", "gcc")
        d.setVar("CXX_host", "g++")
        d.setVarFlag("npm_config_arch", "export", True)
        d.setVarFlag("CC_host", "export", True)
        d.setVarFlag("CXX_host", "export", True)
}

DEPENDS += " yarn-native nodejs-native"
YARN="yarn --no-default-rc --use-yarnrc=.yarnrc --offline --pure-lockfile"
