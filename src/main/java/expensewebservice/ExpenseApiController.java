//------------------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//------------------------------------------------------------------------------

package expensewebservice;

import java.net.*;
import java.net.URL;
import java.text.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExpenseApiController {

    @RequestMapping(value="/api/expense", method=RequestMethod.POST)
    public ResponseEntity<?> post(
        @RequestHeader(value="Authorization") String auth) {
        
        // Replace https://api.contoso.com with your service domain URL.
        // For example, if the service URL is https://api.xyz.com/finance/expense?id=1234,
        // then replace https://api.contoso.com with https://api.xyz.com
        ActionableMessageTokenValidationResult result = validateToken(auth, "https://api.contoso.com");
        
        if (!result.getValidationSucceeded()) {
            if (result.getError() != null) {
                System.err.println(result.getError().toString());
            }

            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } 
        
        // We have a valid token. We will verify the sender and the action performer. 
        // You should replace the code below with your own validation logic.
        // In this example, we verify that the email is sent by Contoso LOB system
        // and the action performer has to be someone with @contoso.com email.
        //
        // You should also return the CARD-ACTION-STATUS header in the response.
        // The value of the header will be displayed to the user.
        if (!Objects.equals(result.getSender().toLowerCase(), "lob@contoso.com") ||
            !result.getActionPerformer().toLowerCase().endsWith("@contoso.com")) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("CARD-ACTION-STATUS", "Invalid sender or the action performer is not allowed.");
            return new ResponseEntity<>(null, headers, HttpStatus.FORBIDDEN);
        }
        
        // Further business logic code here to process the expense report.
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("CARD-ACTION-STATUS", "The expense was approved.");
        return new ResponseEntity<>(null, headers, HttpStatus.OK);
    }

    private ActionableMessageTokenValidationResult validateToken(String authHeader, String targetUrl) {
        ActionableMessageTokenValidationResult result = new ActionableMessageTokenValidationResult();

        try {

            String[] tokens = authHeader.split(" ");
            if (tokens.length != 2) {
                result.setError(new IllegalStateException("Invalid token."));
                return result;
            }

            ActionableMessageTokenValidator validator = new ActionableMessageTokenValidator();
            result = validator.validateToken(tokens[1], targetUrl);
        }
        catch (Exception ex) {
            result.setError(ex);
        }

        return result;
    }
}