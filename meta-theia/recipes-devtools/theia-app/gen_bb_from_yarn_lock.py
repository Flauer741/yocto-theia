#!/bin/env python3
# SPDX-License-Identifier: MIT

import re

regex = re.compile(r'^\s*resolved\s+"(https://registry\.yarnpkg\.com/)(@[^"/]+)(/[^"]*/)([^"/]+)#([0-9a-f]{40})"\s*$')
regex2 = re.compile(r'^\s*resolved\s+"(https://registry\.yarnpkg\.com/)([^"]*/)([^"/]+)#([0-9a-f]{40})"\s*$')

files = []

with open('theia-app/yarn.lock', 'r') as yarn:
    for line in yarn.readlines():
        m = regex.match(line)
        if m:
            files.append((
                m.group(1) + m.group(2) + m.group(3) + m.group(4),
                m.group(5),
                m.group(2) + '-' + m.group(4),
                ))
        else:
            m = regex2.match(line)
            if m:
                files.append((
                    m.group(1) + m.group(2) + m.group(3),
                    m.group(4),
                    m.group(3),
                    ))

with open('theia_deps.inc', 'w') as out:
    out.write('SRC_URI += " \\\n')
    for f in files:
        out.write(f'    {f[0]};name={f[1]};downloadfilename={f[2]};subdir=offline_packages;unpack=0 \\\n')
    out.write('"\n\n')
    for f in files:
        out.write(f'SRC_URI[{f[1]}.sha1sum] = "{f[1]}"\n')
    out.write('\n')
