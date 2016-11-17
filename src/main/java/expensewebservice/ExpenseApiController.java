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

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.*;
import com.nimbusds.jwt.proc.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExpenseApiController {

    @RequestMapping(value="/api/expense", method=RequestMethod.POST)
    public ResponseEntity<?> post(
        @RequestHeader(value="Authentication") String auth) throws MalformedURLException, ParseException, BadJOSEException, JOSEException {
        
        try {
            ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
            
            OpenIdMetadata config = new OpenIdMetadata("https://substrate.office.com/sts/common/.well-known/openid-configuration");
            JWKSource keySource = new RemoteJWKSet(new URL(config.getJsonWebKeyUrl()));
            
            String[] tokens = auth.split(" ");
            JWT token = JWTParser.parse(tokens[1]);
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.parse(token.getHeader().getAlgorithm().getName());
        
            JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
            jwtProcessor.setJWSKeySelector(keySelector);
            
            SecurityContext ctx = null;
            JWTClaimsSet claimsSet = jwtProcessor.process(tokens[1], ctx);
            
            if (!verifyClaims(claimsSet)) {
                return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
            }
            
            return new ResponseEntity<>(claimsSet.getIssuer(), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    
    private Boolean verifyClaims(JWTClaimsSet claims) {
        try {
            if (!Objects.equals(claims.getIssuer(), "https://substrate.office.com/sts/")) {
                return false;
            }
            
            List<String> audiences = claims.getAudience();
            if (audiences.size() != 1) {
                return false;
            }
            
            // Replace [WEB SERVICE URL] with your service domain URL.
            // For example, if the service URL is https://api.contoso.com/finance/expense?id=1234,
            // then replace [WEB SERVICE URL] with https://api.contoso.com
            if (!Objects.equals(audiences.get(0), "[WEB SERVICE URL]")) {
                return false;
            }
            
            if (!Objects.equals(claims.getStringClaim("appid"), "48af08dc-f6d2-435f-b2a7-069abd99c086")) {
                return false;
            }
            
            // sender claim will contain the email address of the sender.
            // Validate that the email is sent by your organization.
            String sender = claims.getStringClaim("sender");
            
            // subject claim will contain the email of the person who performed the action.
            // Validate that the person has the priviledge to perform this action.
            String subject = claims.getSubject();
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    }
}