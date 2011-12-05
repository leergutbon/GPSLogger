#!/usr/bin/perl

# I'm using the international data set with measurements every 3 seconds.
# Set to 3601 for 1 second data for the U.S.
$measPerDeg = 1201;

# I am keeping the data in a subdirectory called hgt which is in the same folder as the script.
# If you save the script in a directory in your path like /usr/local/bin you may need to change 
# $dirName accordingly. Keeping it in /opt/srtmdata would make sense.
$x = <>;
$y = <>;
chomp($x);
chomp($y);
printf "lat: %s, len: %s \n", $x,$y;
$x = int (($x - 8.0) * 1201);
$y = int (1201 - (($y - 50.0) * 1201));
printf "row: %s, col: %s \n", $x,$y;



$dirName = "hgt";

chdir $dirName;

# Read only the .hgt files, so they must be unzipped. 
# This will obviously break if you have files that don't come from the SRTM data set with .hgt extensions in the same directory
my @datafiles = glob "*.hgt";

# print the file count so we know how many there are (Don't worry, it's very fast)
print "Processing " . ($#datafiles + 1) . " files.\n";

foreach $hgtfile (@datafiles)
{
        # Open the files in the loop
        open HGTFILE, $hgtfile or die "Error opening $hgtfile. Aborting! $!";
        
        # Specify it's a binary file so Perl doesn't get confused
        binmode HGTFILE;
        
        # Read starting (Southwestern) latitude from the filename. If we're dealing with the southern hemisphere, use negative coords.
        $starty = substr ($hgtfile, 1, 2);
        
        if (substr ($hgtfile, 0, 1) eq "S")
        {
                $starty = -$starty;
        }
        
        # Same for longitude.
        $startx = substr ($hgtfile, 4, 3);
        if (substr ($hgtfile, 3, 1) eq "W")
        {
                $startx = -$startx;
        }
        
        # Always good to print debug information.
        print "File:\t$hgtfile\nCoordinates:\t$starty, $startx\n\n";
        
        # Since the .hgt files use signed short integers, we are reading 2 * 1201 * 1201 bytes.
        # Read the entire file in memory
        $n = read HGTFILE, $data, 2 * $measPerDeg * $measPerDeg;
        
        if ($n == 0)
        {
                print "Could not read file!\n";
        }
        else
        {
                print "$n bytes read.\n";
        }       
        
        $point = 0;
        $offset = 0;
        
        # Finally we loop through the file, yeah
        for ($i = 0; $i < $measPerDeg; $i++)
        {
                for ($j = 0; $j< $measPerDeg; $j++)
                {
                        # Read the 2 binary bytes
                        $short = substr ($data, $offset, 2);
                        # and store the value in the string $shorts
                        $shorts = unpack("s>", $short);
						                        
                        #Print the whole thing to screen.


                        if ($i == $y && $j == $x){
                        #if (($starty + 1 - $i / ($measPerDeg - 1)) > 50.5 && ($starty + 1 - $i / ($measPerDeg - 1)) < 50.6 && $j == 819){
                        printf "Point %7d: %10.8g, %10.8g\tElevation: %7g. (Offset: %d, row: $i, col: $j)\n", 
                                $point, ($starty + 1 - $i / ($measPerDeg - 1)), ($startx + $j / ($measPerDeg - 1)), 
                                $shorts, $offset;
                        }
                        # Skip 2 bytes to read the next value
                        $offset += 2;
                        $point++;
                }
        }

        # All done; close the file.
        close HGTFILE;
        
}