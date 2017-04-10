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

public class ActionableMessageTokenValidationResult {
    private Boolean validationSucceeded;
    private String sender;
    private String actionPerformer;
    private Exception error;
     
    public ActionableMessageTokenValidationResult() {
        validationSucceeded = false;
        sender = "";
        actionPerformer = "";
    }
    
    public Boolean getValidationSucceeded() {
        return validationSucceeded;
    }
    
    public void setValidationSucceeded(Boolean value) {
        validationSucceeded = value;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String value) {
        sender = value;
    }
    
    public String getActionPerformer() {
        return actionPerformer;
    }
    
    public void setActionPerformer(String value) {
        actionPerformer = value;
    }
    
    public Exception getError() {
        return error;
    }
    
    public void setError(Exception value) {
        error = value;
    }
}