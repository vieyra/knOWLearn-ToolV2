from nltk.corpus import wordnet as wn
import nltk

class Term:

  def __init__(self, term, filename=None):
    self.term = term
    self.synsets = [[synset, 1.0, self.getRelatedWords(synset.definition, synset.lemma_names)] for synset in wn.synsets(self.term.replace(' ','_'),wn.NOUN)]
    self.sentences = []
    self.sense = None
    self.senseprobability = 0.0

  def getRelatedWords(self,definition, lemma_names):
    tokenized_definition = nltk.word_tokenize(definition)
    tagged_definition = nltk.pos_tag(tokenized_definition)
    words = []
    for tag in tagged_definition:
      if tag.__getitem__(1).startswith('N'):
        if ((tag.__getitem__(0) in lemma_names) == False) and (tag.__getitem__(0).__len__() > 1):
          words.append(tag.__getitem__(0))
    return words

  def setSentences(self, sentences):
    self.sentences = sentences

  def addSentence(self, sentence):
    self.sentences.append(sentence)

  def disambiguate(self):
    if self.synsets.__len__() == 1:
      self.sense = self.synsets[0][0]
#      if self.isNoun():
      self.senseprobability = 1.0      
    if self.synsets.__len__() > 1:
      for sentence in self.sentences:
        for i in range(0,self.synsets.__len__()):
          synset = self.synsets[i]
          mentions = 1.0
          for word in synset[2]:
            mentions += sentence.count(word)
          self.synsets[i][1] *= (mentions * mentions)
      self.__getsense__()

  def __getsense__(self):
    total = 0.0
    for synset in self.synsets:
      total += synset[1]
    for i in range(0,self.synsets.__len__()):
      self.synsets[i][1] /= total
    max_ = 0
    for i in range(0,self.synsets.__len__()):
      if self.synsets[i][1] > self.synsets[max_][1]:
        max_ = i
    self.sense = self.synsets[max_][0]
    self.senseprobability = self.synsets[max_][1]

  def isNoun(self):
    asnounmentions = 0.0
    for sentence in self.sentences:
      tok_sent = nltk.word_tokenize(sentence)
      tag_sent = nltk.pos_tag(tok_sent)
      for tag in tag_sent:
        if tag.__getitem__(0) == self.term:
          if tag.__getitem__(1).startswith('N'):
            asnounmentions += 1.0
    try:
      print self.term,'; ',asnounmentions,'/',float(self.sentences.__len__())
      return_ = ((asnounmentions / float(self.sentences.__len__())) > 0.5)
      print return_
      return return_
    except:
      return False
        
