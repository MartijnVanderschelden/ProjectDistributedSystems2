# Gedistribueerde Systemen 2 - Horeca Contact Tracing

## Volgorde opstarten services
1. Registrar
2. Matching
3. MixingProxy
4. Catering
5. User
6. Doctors

## Werkwijze
### Catering registreren
Business number moet tussen [1-5] liggen.
Zowel het business nummer, telefoonnummer en de locatie moeten uniek zijn.  
Locatie mag vrij gekozen worden
  
Per dag krijgt de catering een unieke QR code die het laat scannen door de bezoekende users.

### User registreren
Enkel telefoonnummer moet uniek zijn  
Vervolgens krijgt de user 48 unieke tokens per dag om zich te registreren bij een catering. Bij ieder bezoek, verbruikt de user 1 token.
  
De user kan de QR code van de catering scannen (ingeven) wanneer het een catering bezoekt. 
Bij het einde van zijn bezoek kan die de registratie stopzetten door op de "Exit" knop te drukken.  
Wanneer een user een catering bezoekt, worden de logs opgeslagen in een apart txt bestand: `log_"phonenumber".txt`. In dit bestand wordt er per regel het tijdstip, de QR code van de catering en zijn persoonlijke usertoken die hij dan gebruikt heeft opgeslagen.

De volledige applicatie kan afgesloten worden door op "Stop application" te drukken.

### Doctor
Wanneer een user positief test bij de doctor, geeft de doctor het telefoonnummer van de user door. 
De doctor vraagt de logs op van de afgelopen twee dagen en tekent die. De getekende logs worden vervolgens verstuurd naar de `matchingService`

### MatchingService
Bij een positieve user ontvangt de `matchingService` de getekende logs. Die controleert de authenticiteit van de logs.   
Aan de hand van de `registrar` wordt de catering die de positieve user bezocht heeft op de hoogte gebracht.  
De `registrar` krijgt ook alle tokens die moeten geinformeerd worden. Elke nieuwe dag worden deze door gestuurd naar alle users.
De users controleren vervolgens of deze tokens voorkomen in hun logs. Indien dit het geval is, krijgen ze een melding dat ze in contact zijn gekomen met een positief getest persoon
