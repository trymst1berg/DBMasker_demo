package no.bank.transformations;
 
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.transformations.CreditCard;
 
/**
 * Calculate the check-digits in a Norwegian Tax ID.
 *
 */
public class Person_NO implements ITransformation{
 
    public static final String LABEL = "Person_NO - Norwegian ID generation";
     
    @Override
    public String transform(String input) {
        if(input == null || input.isEmpty())
            return input;
        String s1 = input.substring(0, 9);
        String s2 = personCheckDigit(s1);
        if(s2.length()>2){
            // If invalid number try next
            String s3=""+(Integer.parseInt(input.substring(6, 9))+1002);
            return toPersonNO(input.substring(0, 6)+s3.substring(1, 4)+input.substring(9, 11));
        }
        return s1+s2;
    }
 
    /**
     * Fix last two digits of a Norwegian Social Security Number
     * <br>It uses MOD-11 thus it may still create an invalid number 
     * @param value to be corrected
     * @return corrected value
     */
    public static String toPersonNO(String value) {
        String s1 = value.substring(0, 9);
        String s2 = personCheckDigit(s1);
        if(s2.length()>2){
            // If invalid number try next
            String s3=""+(Integer.parseInt(value.substring(6, 9))+1002);
            return toPersonNO(value.substring(0, 6)+s3.substring(1, 4)+value.substring(9, 11));
        }
        return s1+s2;
    }
     
    static String personCheckDigit(String value) {
        int[] d = CreditCard.getDigits(value);
        int k1 = 11 - ((3 * d[0] + 7 * d[1] + 6 * d[2] + 1 * d[3] + 8 * d[4] + 9 * d[5]
                + 4 * d[6] + 5 * d[7] + 2 * d[8]) % 11);
        int k2 = 11 - ((5 * d[0] + 4 * d[1] + 3 * d[2] + 2 * d[3] + 7 * d[4] + 6 * d[5]
                + 5 * d[6] + 4 * d[7] + 3 * d[8] + 2 * k1) % 11);
        return k1+""+k2;
    }
 
    public static void main(String[] args) {
        String pnr = "02076829716";
        System.out.println("Encode PNR number '" + pnr + "': " + toPersonNO(pnr));
         
    }
 
}