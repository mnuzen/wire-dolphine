import requests
import json

urls = [
  'google.com',
  'reddit.com',
  'nytimes.com',
  'reddit.com',
  'facebook.com',
  'python.org',
  'imgur.com',
  'wikipedia.org',
  'youtube.com',
  'stackoverflow.com',
]

def lookup(url, t):
  rlookup = 'https://dns.google.com/resolve?name={}&type={}'.format(url, t)
  return requests.get(rlookup)

def lookupUrl(url):
  res = lookup(url, 'A')
  res = res.json()
  return res['Answer'][0]['data']

def reverseLookup(ip):
  ip = ip.split('.')
  ip.reverse()
  res = lookup('.'.join(ip) + '.in-addr.arpa', 'PTR')
  res = res.json()
  return res['Answer'][-1]['data']

for url in urls:
  ip = lookupUrl(url)
  host = reverseLookup(ip)
  print("{} - {}: {}".format(url, ip, host))