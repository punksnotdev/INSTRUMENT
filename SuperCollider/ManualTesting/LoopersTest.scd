s.boot // Boot the server, if you need to


b = IdentityDictionary.new;
l = IdentityDictionary.new;

b[0] = Buffer.alloc(s, s.sampleRate * 3, 1 );
b[1] = Buffer.alloc(s, s.sampleRate * 3, 1 );
b[2] = Buffer.alloc(s, s.sampleRate * 3, 1 );

r=Synth(\loopWrite,[\inBus,0,\buffer,b[0]]);
r=Synth(\loopWrite,[\inBus,0,\buffer,b[1]]);
r=Synth(\loopWrite,[\inBus,0,\buffer,b[2]]);

r.free;

l[0] = Synth(\loopRead,[\buffer, b[0]]);
l[1] = Synth(\loopRead,[\buffer, b[1]]);
l[2] = Synth(\loopRead,[\buffer, b[2]]);


l[0].free;
l[1].free;
l[2].free;

// l.free;
