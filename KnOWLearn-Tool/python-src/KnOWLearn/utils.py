# -*- coding: utf-8 -*-
from nltk.corpus import wordnet as wn
import pickle

import nltk

top_level_synsets = [wn.synset('entity.n.01'), wn.synset('thing.n.08'), wn.synset('physical_entity.n.01'), wn.synset('abstraction.n.06'), wn.synset('jimdandy.n.02'), wn.synset('stinker.n.02'), wn.synset('change.n.06'), wn.synset('horror.n.02'), wn.synset('freshener.n.01'), wn.synset('pacifier.n.02'), wn.synset('security_blanket.n.01'), wn.synset('whacker.n.01'), wn.synset('matter.n.03'), wn.synset('process.n.06'), wn.synset('thing.n.12'), wn.synset('substance.n.04'), wn.synset('object.n.01'), wn.synset('causal_agent.n.01'), wn.synset('communication.n.02'), wn.synset('group.n.01'), wn.synset('otherworld.n.01'), wn.synset('psychological_feature.n.01'), wn.synset('attribute.n.02'), wn.synset('set.n.02'), wn.synset('measure.n.02'), wn.synset('relation.n.01')]

stopwords = pickle.load(open('KnOWLearn/sources/stopwords'))


class parser:	
  def __init__(self, filename=None):
    self.grammar = r"""
      NP: {((<NN.*>+)* <POS>*)*<NN.*>+}
    """
    self.cp = nltk.RegexpParser(self.grammar)
  
  def parsText(self, text):
    texttok = nltk.word_tokenize(text)
    texttag = nltk.pos_tag(texttok)
    textpars = self.cp.parse(texttag)
    return self.getNP(textpars)

  def getNP(self, textpars):
    NPs = []
    for e in textpars:
      try:
        cad = ""
        for e1 in e:
          cad += str(e1.__getitem__(0))+" "
        if cad.__len__() > 4:
          NPs.append(cad[:-1])
      except:
        print "Error"
    return NPs

  def __del__(self):
    del self

class stringlist(list):
  def __init__(self):
    list.__init__(self)

  def append(self, string):
    if not (normalizeString(string) in self):
      list.append(self, normalizeString(string))

  def extend(self, List):
    for element in List:
      self.append(element)

class freqstringlist(list):
  def __init__(self):
    list.__init__(self)
    self.strings = []
    self.freq = []

  def append(self,string):
    st = normalizeString(string)
    if (st in self.strings):
      index = self.strings.index(st)
      self.freq[index] += 1
      self[index][0] = self.freq[index] 
    else:
      list.append(self,[1,st])
      self.freq.append(1)
      self.strings.append(st)

  def index(self, string):
    return self.strings.index(string)

def normalizePluralword(word):
  if word.endswith('s'):
    for synset in wn.synsets(word,wn.NOUN):
      for lemma_name in synset.lemma_names:
        if lemma_name.startswith(word[:-3]) and lemma_name.__len__() < word.__len__():
          return lemma_name
  return word

def normalizePhrase(string_):
  term = ''
  if string_.endswith('s'):
    splitedterm = string_.split()  
    for i in range(0, splitedterm.__len__() -1):
      term += splitedterm[i] + ' '
    term += normalizePluralword(splitedterm[splitedterm.__len__() - 1])
    return term
  return string_
    
def normalizeString(string_):
  if string_.count(' ') > 0:
    return normalizePhrase(string_.lower())
  else:
    return normalizePluralword(string_.lower())
  

  
