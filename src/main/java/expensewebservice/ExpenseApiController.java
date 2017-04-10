// 
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
// 
// Copyright (c) Microsoft Corporation
// All rights reserved.
// 
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package expensewebservice;

import java.net.*;
import java.net.URL;
import java.text.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExpenseApiController {

    @RequestMapping(value="/api/expense", method=RequestMethod.POST)
    public ResponseEntity<?> post(
        @RequestHeader(value="Authorization") String auth) {
        
        try {
            String[] tokens = auth.split(" ");
            ActionableMessageTokenValidator validator = new ActionableMessageTokenValidator();
            
            // Replace [WEB SERVICE URL] with your service domain URL.
            // For example, if the service URL is https://api.contoso.com/finance/expense?id=1234,
            // then replace [WEB SERVICE URL] with https://api.contoso.com
            ActionableMessageTokenValidationResult result = validator.validateToken(tokens[1], "[WEB SERVICE URL]");
            
            if (!result.getValidationSucceeded()) {
                return new ResponseEntity<>(result.getError().toString(), HttpStatus.UNAUTHORIZED);
            } 
            
            // We have a valid token. We will verify the sender and the action performer. 
            // In this example, we verify that the email is sent by Contoso LOB system
            // and the action performer has to be someone with @contoso.com email.
            if (!Objects.equals(result.getSender().toLowerCase(), "lob@contoso.com") ||
                !result.getSender().toLowerCase().endsWith("@contoso.com")) {
                return new ResponseEntity<>("Invalid sender or the action performer is not allowed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            return new ResponseEntity<>("validation succeeded", HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}