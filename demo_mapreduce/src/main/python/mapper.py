#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys


def map():
    for line in sys.stdin:
        line = line.strip()
        words = line.split()
        for word in words:
            print '%s\t%s' % (word, 1)


if __name__ == '__main__':
    map()