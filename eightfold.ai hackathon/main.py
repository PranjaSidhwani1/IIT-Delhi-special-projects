import re
import requests
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import PyPDF2


your_token = "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"


input_query="Notable achievement in Software development"          ##Input the query here 
input_resume_path="Resume_3.pdf"         ## Place the input resume in the directory and enter the name of the resume pdf
                                        ## Run the rest of the code as it is.
                                        ## Output will be saved as highlighted_{name of the input_resume}


output=[]


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}
prompt=input_query
def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]
output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}

prompt=f"Generate technical keywords related to {input_query}"

def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
	
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]

output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}
prompt=f"Write skills related to {input_query}"
def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]
output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}
prompt=f"Write positions related to {input_query}"
def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]
output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}
prompt=f"Write job profile related to {input_query}"
def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]
output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


API_URL = "https://api-inference.huggingface.co/models/google/gemma-2b-it"
headers = {"Authorization": "Bearer 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"}
prompt=f"Write education background related to {input_query}"
def query(payload,prompt):
	response = requests.post(API_URL, headers=headers, json=payload)
	return response.json()
output_technical_keywords = query({
	"inputs":prompt ,
},prompt)[0]["generated_text"]
output_technical_keywords= output_technical_keywords.replace("\n", "")
output_technical_keywords= re.sub(r'[^\w\s]', '', output_technical_keywords)
output_technical_keywords = output_technical_keywords.replace(prompt, "")
output.append(output_technical_keywords)


text_to_match= " ".join(output)


def extract_text_from_pdf(pdf_path):
    pdf_text = ""
    with open(pdf_path, "rb") as f:
        pdf_reader = PyPDF2.PdfReader(f)
        for page_num in range(len(pdf_reader.pages)):
            page = pdf_reader.pages[page_num]
            pdf_text += page.extract_text()
    return pdf_text

def extract_lines(text):
    lines = []
    temp_line = ""

    for char in text:
        if char == '.' or char == ',':
            temp_line += char
            temp_line = temp_line.replace("\n", " ")
            lines.append(temp_line.strip())
            temp_line = ""
        elif char == '•' or char == '●' or char=="#" or char==":":  # Adding '\n' for line breaks and bullet points
            temp_line = temp_line.replace("\n", " ")
            lines.append(temp_line.strip())
            temp_line = ""
        else:
            temp_line += char

    if temp_line.strip():
        temp_line = temp_line.replace("\n", " ")
        lines.append(temp_line.strip())

    return lines

# Path to your PDF file
pdf_path = input_resume_path

# Extract text from PDF
resume_text = extract_text_from_pdf(pdf_path)

# Extract lines based on full stops, commas, and bullet points
lines = extract_lines(resume_text)


from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def calculate_cosine_similarity(line, total_text):
    tfidf_vectorizer = TfidfVectorizer()
    tfidf_matrix = tfidf_vectorizer.fit_transform([total_text])
    line_tfidf = tfidf_vectorizer.transform([line])
    similarity_scores = cosine_similarity(line_tfidf, tfidf_matrix)
    return similarity_scores[0][0]  # Return the similarity score

def give_similarity_coeff(text1, text2):
    text1 = text1.replace('\n', ' ')  # Replace newline characters with space
    text2 = text2.replace('\n', ' ')  # Replace newline characters with space
    text1_lower = text1.lower()
    text2_lower = text2.lower()
    return calculate_cosine_similarity(text1_lower, text2_lower)


weights=[]
for x in lines:
    similarity_coefficient=give_similarity_coeff(x,text_to_match)
    weights.append((x,similarity_coefficient))
weights.sort(key=lambda x:x[1],reverse=True)


import fitz  # PyMuPDF

def highlight_pdf(pdf_path, output_path, lines):
    # Open the PDF
    pdf_document = fitz.open(pdf_path)

    j = 0  # Initialize counter for lines
    for line in lines:
        j += 1  # Increment line counter
        for page_number in range(len(pdf_document)):
            page = pdf_document[page_number]
            text = line.strip()

            # Extract text from the page
            page_text = page.get_text()

            if text in page_text:
                # Find all instances of the text
                text_instances = page.search_for(text)
                for inst in text_instances:
                    # Determine the appropriate color for the rectangle based on the line count
                    if j < len(lines) // 3:               
                        color = (1, 0.647, 0)  # Orange color
                        fill=(1, 1, 0.9)
                    elif j >= len(lines) // 3 and j <= 2 * len(lines) // 3:
                        color = (1, 1, 0)  # Yellow color
                        fill=(1, 1, 0.8)
                    else:
                        color = (1, 1, 0)  # Yellow color
                        fill=(1, 1, 0.9)
                    # Draw rectangle over text as highlight with specified color
                    page.draw_rect(inst, color=color, fill=fill, width=1.5,overlay=False)
#                     highlight = page.add_highlight_annot(inst)
                    
    pdf_document.save(output_path)
    pdf_document.close()

# Usage
line_text = []
for j in range(0, int(0.2 * len(weights))):
    line_text.append(weights[j][0])
input_pdf_path = input_resume_path
output_pdf_path = "highlighted_" + input_pdf_path
highlight_pdf(input_pdf_path, output_pdf_path, line_text)


print("TEXT HIGHLIGHTED:")
for j in range (0,len(line_text)):
    print(line_text[j])



