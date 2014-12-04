# -*- coding: utf-8 -*-
from KnOWLearn.TermExtractor.CandidateTerms import CandidateTerms as extractTerms
from KnOWLearn.WSDisambiguator.Disambiguator import Disambiguator as disambiguateTerms

import socket
import sys

def getMDTerms(disambiguateTerms):
  metadataTerms = ''
  for i in range(0,disambiguateTerms.__len__()):
    term = Senses.termsdis[i]
    metadataTerms += str(term.term)+'/TermName/'
    if term.senseprobability > 0.5:
      metadataTerms += str(term.sense.name)+'/TermSense/'
    else:
      metadataTerms += 'None/TermSense/'
    for j in range(0,term.synsets.__len__()):
      synset = term.synsets[j][0]
      metadataTerms += str(synset.name)+'/SynsetName/'
      metadataTerms += str(synset.definition)+'/SynsetDefinition/'
      if(term.synsets.__len__() > j):
        metadataTerms += '/Synset/'
    if(Senses.termsdis.__len__() > i):
      metadataTerms += '/Term/'
  return metadataTerms



if __name__ == "__main__":
  Terms = []
  Senses = None
  HOST = 'localhost'                 	# Symbolic name meaning the localhost
  PORT = 50003              		# Arbitrary non-privileged port
  if len(sys.argv) > 1:
    PORT = int(sys.argv[1])
  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  s.bind((HOST, PORT))
  s.listen(1)
  print (HOST, PORT)
  conn, addr = s.accept()
  print 'Connected by', addr
  platform = sys.platform
  charToDelete = 2
  if platform == 'linux2':
    charToDelete = 1
  while 1:
    data = conn.recv(1024)
    print 'Received: '+data
    if not data: break
    elif data.startswith('terms for'):
      directory = str(data.split('Path:')[1])[:-(charToDelete)]
      directory = directory.strip()
      Terms = extractTerms(str(directory),0.05,0.005)
      print 'Terms founded: ',Terms
      conn.send(str(Terms)+'\n')
    elif data.startswith('recompute: '):
      values = data[11:-(charToDelete)].split(',')
      print 'recomputing terms with ',values
      Terms.recompute(float(values[0]),float(values[1]))
      print 'Terms recomputed: ',Terms
      conn.send(str(Terms)+'\n')
    elif data.startswith('disambiguate terms'):
	  Senses = disambiguateTerms(Terms)
	  conn.send(getMDTerms(Senses.termsdis)+'\n')
    elif data.startswith('delete term: '):
	  trashterm = str(data[13:-(charToDelete)])
	  print trashterm, ' to delete'
	  Terms.trash(trashterm)
	  print Terms
	  conn.send(str(Terms)+'\n')
    else:
      conn.send(data)
  conn.close()
  addr.close()

  
