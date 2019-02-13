# -*- coding: utf-8 -*-
"""
Created on Sun Jan 13 16:21:08 2019

@author: Roman_Moiseiev
"""

from confluent_kafka import Producer
import uuid

testData = ["Claudius, King of Denmark.",
            "Marcellus, Officer.",
            "Hamlet, son to the former, and nephew to the present king.",
            "Polonius, Lord Chamberlain.",
            "Horatio, friend to Hamlet."]

p = Producer({'bootstrap.servers': 'localhost:39092'})
for text in testData:
    p.produce('foo', key=str(uuid.uuid4()), value=text)
p.flush(30)