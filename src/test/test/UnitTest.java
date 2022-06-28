// Generated with g9 DBmasker.

package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {
   // Repositories 
   @Autowired    
     // undefinedRepository repo; example  
   /* example test 
   @Test
   public void should_get_h2_db_records_for_Customer() {
       CUSTOMER Actual = repo.save(CUSTOMER.builder().CREDITCARD("10001-2000-4000-5000")
               .EMAIL("ek@yahoo.com")
               .NAME("EK")
               .PASSWORD("ghghgh")
               .PHONE("4444444")
               .LOCK_FLAG(1)
               .CUSTODIAN(1)
               .build());
       Optional<CUSTOMER> expected=repo.findById(Actual.getCUSTOMERNO());
       Assert.assertEquals(Actual.getNAME(),expected.get().getNAME());

   }
   */   
  @Test
   public void should_get_h2_db_records() {
        // write your test here 
 }
}

