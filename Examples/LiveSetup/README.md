# livecoding setup

## Instrucciones

1. Abrir **start.scd**

2. Correr el bloque entre paréntesis

```SuperCollider
(
	s.options.memSize=2048*1024;
	s.options.maxNodes=128*1024;
	s.boot;
)
```

3. Correr la línea siguiente


```SuperCollider
(thisProcess.nowExecutingPath.dirname ++ "/tools/setup/setup.scd").load;
```

4. Esperar

5. Abrir **parts/01.scd**
