import requests
import json
import time

urls = open('../resources/ips.txt', 'r').readlines()

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
  return res.text

def getall():
  for url in urls:
    host = reverseLookup(url.split(" ")[0])
    print("{} - {}".format(url, host))

millis = int(round(time.time() * 1000))
getall()
ctm = int(round(time.time() * 1000))
print("time: " + str(ctm - millis))

# 64501ms