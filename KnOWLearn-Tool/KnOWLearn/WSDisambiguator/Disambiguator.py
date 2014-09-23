from Term import Term
import nltk
import random

class Disambiguator:

  def __init__(self, terms, filename=None):
    self.terms = terms
    self.termsdis = []
    for term in terms:
      self.termsdis.append(Term(term))
    self.disambiguate()      
      
  def disambiguate(self):
    num_sentences = 20
    if (self.terms.db.D.__len__() / 2) < num_sentences:
      num_sentences = (self.terms.db.D.__len__() / 2)
    for term in self.termsdis:
      self.getSentences(term, num_sentences)
      term.disambiguate()

  def getSentences(self, term, num_sentences):
    for document in self.terms.db.D:
      for sentence in nltk.sent_tokenize(document.abstract):
        if sentence.count(term.term) > 0:
          term.addSentence(sentence)
 #   c = 0  for d in self.terms.db.D:
  #  lendocs = self.documents.__len__() - 1
 #   while c < num_sentences:
#      i = random.randint(0, lendocs)
 #     j = random.randint(0, (self.documents[i].__len__() - 1))
 #     if self.documents[i][j].count(term.term) > 0:
 #       if (self.documents[i][j] in term.sentences) == False:
 #         term.addSentence(self.documents[i][j])
 #         c += 1

