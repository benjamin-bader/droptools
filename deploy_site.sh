#!/usr/bin/env bash

cat README.md | grep -v "Build Status" | grep -v "Coverage Status" > docs/index.md

mkdocs gh-deploy

