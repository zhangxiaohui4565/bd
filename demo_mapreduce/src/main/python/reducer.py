#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
from itertools import groupby


def from_stdin():
    for line in sys.stdin:
        word, count = line.strip().split('\t')
        yield(word, count)


def reduce():
    # example: [(apple, 1), (apple, 1), (apple, 1), (orange, 1), (orange, 1)]
    # example: word = apple, group = [(apple, 1), (apple, 1), (apple, 1)]
    # example: word = orange, group = [(orange, 1), (orange, 1)]
    # 将key函数用于原循环器的各个元素，根据key函数的结果，将拥有相同函数结果的元素分到一个新的循环器中
    for word, group in groupby(from_stdin(), key = lambda x : x[0]):
        count = sum([int(tup[1]) for tup in group]) # [1, 1, 1]
        print '%s\t%s' % (word, count)


if __name__ == '__main__':
    reduce()