# -*- coding: utf-8 -*-
import sys
import nltk
import re

from KnOWLearn.Document import opendoc
from KnOWLearn.utils import stopwords
from nltk.corpus import wordnet as wn

class DBDocs:

  def __init__(self, filename_, minsupp_threshold, filename=None):
    self.D = opendoc(filename_)     # Set of Documents D ={D1 ,..., Dn}
    self.S = []     # Subsets of terms in all documents
    self.coverS = []# Cov of each term subset
    self.F = []     # Term subset frequents
    self.T = []     # Set of candidate terms

    self.minsupp = minsupp_threshold
    for d in self.D:
      self.getTerms(d) # We get all terms in each document

    for d in self.D:   # We get subsets of frequent terms in each 
      self.getSubterms(d.FT)  # document

    for s in self.S:   #We get the cover value for each term subset
      self.getCover(s)

    self.getSubsetFrequents() # We get the candidate terms with minsupp threshold

    self.getCandidateTerms()#We get the candidate terms of subset frequents


# Function definitions
  def getSubsetFrequents(self):
    for f in self.coverS: 
      if f[1].__len__() >= (self.minsupp * self.D.__len__()):
        self.F.append(f)

  def getCandidateTerms(self):
    for f in self.F:    
      for t in f[0]:
        if (t in self.T) == False:
          if wn.synsets(t) != []:
            for synset in wn.synsets(t, wn.NOUN):
              if(t.lower().startswith(synset.name.split('.')[0])):
                self.T.append(t)
                break
          else:
            self.T.append(t)


  def getCandidateTermsMinsupp(self, min):
    self.T = []
    self.F = []
    self.minsupp = min
    self.getSubsetFrequents()
    self.getCandidateTerms()

  
  def getTerms(self, d):
    sentences = nltk.sent_tokenize(d.abstract)
    tokenized_sentences = [ nltk.word_tokenize(sentence) for sentence in sentences ]
    for sentence in tokenized_sentences:
     for word in sentence:
       if (word in stopwords) == False:
         d.addTerm(word)
    d.getFT()

  def validTerm(self, term):
    pattern = re.compile('(.\.)|([0-9]+)')
    if pattern.match(term[1]) != None or term[1].__len__()==1:
      return False
    elif int(term[0]) > 1:
      return True
    else:
      return False

  def getSubterms(self,FT):
    a = []                 #podría modificarse el tipo de lista
    for i in range(0,9):
      if FT.__len__() > i and self.validTerm(FT[i]):
        a.append(FT[i][1])
    self.getSubsets(a)

  def getSubsets(self,List):
    n = List.__len__()
    if n > 3:
      for i in range( 0, n - 3):
        for j in range( i + 1, n - 2):
          for k in range( j + 1, n - 1):
            for l in range( k + 1, n ):
              a = [List[i],List[j],List[k],List[l]]
              a.sort()
              if (a in self.S) == False:
                self.S.append(a)

  def getCover(self, List):
    coverdocs = []
    for d in self.D:
      if List[0] in d.T and List[1] in d.T and List[2] in d.T and List[3] in d.T:
        coverdocs.append(self.D.index(d))
    self.coverS.append([List,coverdocs])
