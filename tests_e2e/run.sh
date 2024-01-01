#!/bin/bash
set -euo pipefail

python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python run.py "$@"
