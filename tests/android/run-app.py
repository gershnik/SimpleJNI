#
# Copyright 2023 SmJNI Contributors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import sys
import json
import subprocess
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--sdk', type=Path, required=True, help="Android SDK location")
parser.add_argument('--outdir', type=Path, required=True, help="Directory to store output files in")
parser.add_argument("apk", type=Path)

args = parser.parse_args()

sdk: Path = args.sdk
apk: Path = args.apk
outdir: Path = args.outdir

adb = sdk / 'platform-tools/adb'

sdcard = subprocess.run([adb, 'shell', 'echo', '$EXTERNAL_STORAGE'], check=True, stdout=subprocess.PIPE, stderr=sys.stderr, encoding='utf-8').stdout.strip()

print(f'Device external storage is at {sdcard}')

subprocess.run([adb, 'install', '-r', '-d', apk], check=True)
subprocess.run([adb, 'shell', 'pm', 'grant', 'com.example.smjni_test', 'android.permission.WRITE_EXTERNAL_STORAGE'], check=True)
subprocess.run([adb, 'shell', 'pm', 'grant', 'com.example.smjni_test', 'android.permission.READ_EXTERNAL_STORAGE'], check=True)
callres = subprocess.run([adb, 'shell', 'content', 'call', '--uri', 'content://com.example.smjni_test.provider', '--method', 'blah'], 
                         check=True, stdout=subprocess.PIPE, stderr=sys.stderr, encoding='utf-8').stdout.strip()
if callres != 'Result: Bundle[{}]':
    print(callres)
    sys.exit(1)
subprocess.run([adb, 'pull', f'{sdcard}/Download/smjni_test/results.json', outdir], check=True)


with open(outdir / 'results.json', 'r') as resultsFile:
    results = json.load(resultsFile)

print(results['output'])
sys.exit(results['result'])
