#!/usr/bin/env python3
# SPDX-License-Identifier: MIT

import json
import urllib.request
from urllib.parse import urlsplit, unquote
from hashlib import sha1
from pathlib import PurePath

plugins = []

with open('theia-app/package.json', 'r') as f:
    package = json.loads(f.read())

plugins_dir = package.get("theiaPluginsDir", "plugins")

for name, url in package['theiaPlugins'].items():
    print(f"Downloading {name}")
    with urllib.request.urlopen(url) as f:
        checksum = sha1(f.read()).hexdigest()
    filename = PurePath(unquote(urlsplit(url).path)).stem + ".zip"
    plugins.append((name, url, checksum, filename))

with open('plugins.inc', 'w') as out:
    out.write('SRC_URI += " \\\n')
    for p in plugins:
        out.write(f'    {p[1]};downloadfilename={p[3]};name={p[2]};subdir=yarn/{plugins_dir}/{p[0]};unpack=1 \\\n')
    out.write('"\n\n')

    for p in plugins:
        out.write(f'SRC_URI[{p[2]}.sha1sum] = "{p[2]}"\n')
    out.write('\n')

