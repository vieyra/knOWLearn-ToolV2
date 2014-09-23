# -*- coding: utf-8 -*-
from KnOWLearn.TermExtractor.CandidateTerms import CandidateTerms as extractTerms
from KnOWLearn.WSDisambiguator.Disambiguator import Disambiguator as disambiguateTerms

if __name__ == "__main__":
  _filename = '../data/breast_cancer.txt'

# Para extraer términos:
  print '*********Buscando términos en ',_filename,' ***********'
  Terms = extractTerms(_filename,0.05,0.005) 
# Puedes extraer términos de un archivo plano como 'data/ontology_learning.txt' y tomará cada línea como un documento distinto
# O bien un archivo como 'data/ontology_learning.xml' tomará cada nodo 'Document' como un documento distinto
# O bien una carpeta como 'data/ontology_learning' conteniendo un conjunto de documentos XML como los que se encuentran dentro de esta carpeta
# El primer umbral sirve para filtrar los términos simples entre más cercano a 0 sea este umbras más términos simples tendrás
# El segundo umbral tiene la misma funcionalidad que el primero pero con términos compuestos, procura que sea menor al primer umbral
# Puedes volver a filtrar los términos con el siguiente método sin necesidad de hacer todo el procesamiento desde cero
#  Terms.recompute(0.1,0.01) 
  print 'Se encontraron los siguientes términos:'
  for term in Terms:
    print term

  print '\n*************Desambiguando términos***************'
# Para desambiguar términos: para utilizar este maneja archivos planos, se me había olvidado también modificar este módulo xD.
  OntologyLearningSenses = disambiguateTerms(Terms)
  for term in OntologyLearningSenses.termsdis:
    if term.sense:
      if term.senseprobability > 0.5:
        print '\tTerm ',term.term,' disambiguate as:\n\t\t ',term.sense,': ',term.sense.definition,' with a ',term.senseprobability,'% of probability\n'
      else:
        print '\tTerm ',term.term,' is discarded as relevant term\n'
    else:
      print '\tTerm ',term.term,' not have a sense in WordNet\n'

# def getMDTerms(disambiguateTerms):
  # metadataTerms = ''
  # for i in range(0,disambiguateTerms.__len__()):

    # term = Senses.termsdis[i]

    # metadataTerms += str(term.term)+'/TermName/'

    # if term.senseprobability > 0.5:

      # metadataTerms += str(term.sense.name)+'/TermSense/'

    # else:

      # metadataTerms += 'None/TermSense/'

    # for j in range(0,term.synsets.__len__()):

      # synset = term.synsets[j][0]

      # metadataTerms += str(synset.name)+'/SynsetName/'

      # metadataTerms += str(synset.definition)+'/SynsetDefinition/'

      # if(term.synsets.__len__() > j):
        # metadataTerms += '/Synset/'

    # if(Senses.termsdis.__len__() > i):
      # metadataTerms += '/Term/' return 
	
