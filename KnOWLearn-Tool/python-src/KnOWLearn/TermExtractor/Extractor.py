# -*- coding: utf-8 -*-
import nltk
import sys
import pickle
import re

from KnOWLearn.TermExtractor.DBDocs import DBDocs as DB
from KnOWLearn.utils import stopwords
from nltk.corpus import wordnet as wn

class Extractor:

  def __init__(self, filename_, minsupp,thrshld):
    self.db = DB(filename_, minsupp)

    self.CandidateTerms = []
    self.FreqCandidates = []
    self.Candidates = []   
    self.threshold = thrshld
    self.terms = []
    self.terms.extend(self.db.T)
    self.getCandidates()

    totalmensions = 0.0
    for candidate in self.Candidates:
      totalmensions += candidate[0]
    for candidate in self.Candidates:
      candidate[0] /= totalmensions
      if candidate[0] > self.threshold:
        self.terms.append(candidate[1])

    self.normalizeterms()
 
  def getCandidates(self):
    for d in self.db.D:
      for sentence in nltk.sent_tokenize(d.abstract):
        words = nltk.word_tokenize(sentence)
        for word in words:
          if word in self.db.T:
            self.getPhrases(words, words.index(word))

    for candidate in self.CandidateTerms:
      self.Candidates.append([self.FreqCandidates[self.CandidateTerms.index(candidate)],candidate])
    self.Candidates.sort()
    self.Candidates.reverse()

  def filterCandidates(self, delete):
    totalmensions = 0.0
    for candidate in self.Candidates:
      totalmensions += candidate[0]
    for candidate in self.Candidates:
      candidate[0] /= totalmensions
      if delete and candidate[0] < self.threshold:
        self.Candidates.remove(candidate)
       
  def addCandidate(self, candidate ):
    if candidate in self.CandidateTerms:
      self.FreqCandidates[self.CandidateTerms.index(candidate)] += 1
    else:
      self.CandidateTerms.append(candidate)
      self.FreqCandidates.append(1.0)

  def canbeCandidate(self, word ):
    pattern = re.compile('(.\.)|([0-9]+)')
    if word in stopwords:
      return False
    elif pattern.match(word) != None or word.__len__()==1:
      return False
    else:
      return True

  def getPhrases(self, List, i):
    firstword = ''
    lastword = ''
    if i > 0:
      if self.canbeCandidate(List[i - 1]):
        firstword = List[i-1]
    if List.__len__() > (i + 1):
      if self.canbeCandidate(List[i + 1]):
        lastword = List[i+1]
    if firstword != '':
      self.addCandidate(firstword + ' ' + List[i])
    if lastword != '':
      self.addCandidate(List[i] + ' ' + lastword)
    if firstword != '' and lastword != '':
      self.addCandidate(firstword + ' ' + List[i] + ' ' + lastword)

  def normalizePluralword(self, word):
    if word.endswith('s'):
      for synset in wn.synsets(word,wn.NOUN):
        for lemma_name in synset.lemma_names:
          if lemma_name.startswith(word[:-3]) and lemma_name.__len__() < word.__len__():
            return lemma_name
    return word

  def normalizePhrase(self, phrase):
    term = ''
    if phrase.endswith('s'):
      splitedterm = phrase.split()  
      for i in range(0, splitedterm.__len__() -1):
        term += splitedterm[i] + ' '
      term += self.normalizePluralword(splitedterm[splitedterm.__len__() - 1])
      return term
    else:
      return phrase
    
  def normalizeterms(self):
    for i in range(0, self.terms.__len__()):
      if self.terms[i].count(' ') > 0:
        self.terms[i] = self.normalizePhrase(self.terms[i])
      else:
        self.terms[i] = self.normalizePluralword(self.terms[i])
    self.removeRepeatedTerms()

  def removeRepeatedTerms(self):
    for term in self.terms:
      if self.terms.count(term) > 1:
        self.terms.remove(term)

  def recompute(self, mins, thresh):
    self.CandidateTerms = []
    self.FreqCandidates = []
    self.Candidates = []   
    self.threshold = thresh
    self.terms = []
    self.db.getCandidateTermsMinsupp(mins)
    self.terms.extend(self.db.T)
    self.getCandidates()
    totalmensions = 0.0
    for candidate in self.Candidates:
      totalmensions += candidate[0]
    for candidate in self.Candidates:
      candidate[0] /= totalmensions
      if candidate[0] > self.threshold:
        self.terms.append(candidate[1])
    self.normalizeterms()
    

