import struct

class srtmParser(object):

 

    def parseFile(self,filename):

        # read 1,442,401 (1201x1201) high-endian

        # signed 16-bit words into self.z

        fi=open(filename,"rb")

        contents=fi.read()

        fi.close()

        self.z=struct.unpack(">1442401H", contents)

 

    def writeCSV(self,filename):

        if self.z :

            fo=open(filename,"w")

            for row in range(0,1201):

                offset=row*1201

                thisrow=self.z[offset:offset+1201]

                rowdump = ",".join([str(z) for z in thisrow])

                fo.write("%s\n" % rowdump)

            fo.close()

        else:

            return None


#main ?!?

p = srtmParser()
p.parseFile("/home/hagen/Documents/geoinf/N50E008.hgt")
p.writeCSV("/home/hagen/Documents/geoinf/N50E008.csv")
