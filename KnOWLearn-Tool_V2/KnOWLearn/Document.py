#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
from elementtree.ElementTree import parse
class Document:	
  
  def __init__(self):
    self.abstract = ''
    self.title = ''
    self.keywords = []
    self.pathfile = ''
    self.T = []  # Set of terms in Di  
    self.F = []  # Frequenze for each term in T
    self.FT = [] # Frequency y terms 
  
  def getFT(self):
    for t in self.T:
      a = []
      a.append(self.F[self.T.index(t)])
      a.append(t)
      self.FT.append(a)
    self.FT.sort()
    self.FT.reverse()

  def addTerm(self, term):
    if term in self.T:
      index = self.T.index(term)
      self.F[index] += 1
    else:
      self.T.append(term)
      self.F.append(1)


class ReaderXMLDocuments(list):
  def __init__(self, filename_):
    file = open(filename_,'r')
    tree = parse(file)
    root = tree.getroot()
    if root.tag == 'Documents':
      for document in root.getchildren():
        doc = XMLDocument(document)
        doc.pathfile = filename_
        self.append(doc)
    elif root.tag == 'Document':
        doc = XMLDocument(root)
        doc.pathfile = filename_
        self.append(doc)

class XMLDocument(Document):
  def __init__(self, element):
    Document.__init__(self)
    title = element.find('title')
    self.title = title.text.replace('\n  ','').replace('\n','').encode('ascii','replace')
    abstract = element.find('abstract')
    self.abstract = abstract.text.encode('ascii','replace')
    keywords = element.find('keywords')
    if keywords:
      self.keywords = [key.text.encode('ascii','replace') for key in keywords.findall('keyword')]

class ReaderPlainDocuments(list):
  def __init__(self, filename_):
    f = open(filename_)
    while True:
      d = Document()
      l = f.readline()
      if l == '':
        break
      d.abstract = l
      d.pathfile = filename_
      self.append(d)

class PlainDocument(Document):
  def __init__(self, filename_):
    Document.__init__(self)
    self.pathfile = filename_
    f = open(filename_)
    while True:
      l = f.readline()
      if l == '':
        break
      self.abstract += l + '\n'
 
class opendoc(list):
  def __init__(self, path):
    if os.path.isfile(path):				#Path is a file
      if path.endswith('.xml'):
        self.extend(ReaderXMLDocuments((path)))
      elif path.endswith('.txt'):
        self.extend(ReaderPlainDocuments((path)))
    elif os.path.isdir(path):				#Path is a directory
      delimiter = '/'
      if(path.find('\\') != -1):
        delimiter = '\\'
      path += delimiter
      directory = os.path.dirname(path)
      files = os.listdir(directory)
      for _file in files:
        if _file.endswith('.xml'):
          self.extend(ReaderXMLDocuments((path+_file)))
        elif path.endswith('.txt'):
          self.append(PlainDocument((path+_file)))

