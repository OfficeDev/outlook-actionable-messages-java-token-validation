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

/**
* Result from token validation.
*/
public class ActionableMessageTokenValidationResult {
    private Boolean validationSucceeded;
    private String sender;
    private String actionPerformer;
    private Exception error;
    
    /**
    * Constructor.
    */
    public ActionableMessageTokenValidationResult() {
        validationSucceeded = false;
        sender = "";
        actionPerformer = "";
    }
    
    /**
    * Gets the flag that indicates if the validation succeeded.
    * @return Boolean true if the validation succeeded; else false.
    */
    public Boolean getValidationSucceeded() {
        return validationSucceeded;
    }
    
    /**
    * Sets the flag that indicates if the validation succeeded.
    */
    public void setValidationSucceeded(Boolean value) {
        validationSucceeded = value;
    }
    
    /**
    * Gets the value of the sender.
    * @return String The value of the sender.
    */
    public String getSender() {
        return sender;
    }
    
    /**
    * Sets the value of the sender.
    * @param value The value of the sender.
    */
    public void setSender(String value) {
        sender = value;
    }
    
    /**
    * Gets the value of the action performer.
    * @return String The value of the action performer.
    */
    public String getActionPerformer() {
        return actionPerformer;
    }
    
    /**
    * Sets the value of the action performer.
    * @param value The value of the action performer.
    */
    public void setActionPerformer(String value) {
        actionPerformer = value;
    }
    
    /**
    * Gets the exception object if the validation failed.
    * @return Exception The exception object which causes the validation to fail.
    */
    public Exception getError() {
        return error;
    }
    
    /**
    * Sets the exception object.
    * @param value The exception object.
    */
    public void setError(Exception value) {
        error = value;
    }
}