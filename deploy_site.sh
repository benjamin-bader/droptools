#!/usr/bin/env bash

cat README.md | grep -v "Build Status" | grep -v "Coverage Status" | sed 's_docs/__' > docs/index.md

mkdocs gh-deploy

