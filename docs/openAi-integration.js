import OpenAI from "https://cdn.skypack.dev/openai";

const tokenResponse = await fetch(`${backendURL}/api/openai`, {
  method: 'GET',
  headers: {
    "Content-Type": "application/json",
    "Accept": "application/json"
  }
});

if (!tokenResponse.ok) {
    throw new Error(`HTTP error during getting Open AI Auth Token from backend ! status: ${response.status}`);
    }

const GITHUB_TOKEN = await tokenResponse.json();


function getTodayDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0'); 
  const day = String(today.getDate()).padStart(2, '0'); 

  return `${year}-${month}-${day}`;
}

export async function main(command) {
  if (!GITHUB_TOKEN) {
    console.error("Missing GITHUB_TOKEN for backend NLP with OpenAI");
    return;
  }

  const client = new OpenAI({
    baseURL: "https://models.github.ai/inference",
    apiKey: GITHUB_TOKEN,
    dangerouslyAllowBrowser: true 
  });

  try {
    const response = await client.chat.completions.create({
      messages: [
        {
          role: "system",
          content: `You are a task analyzer. Extract the following details from the provided command:
                    1. Task description
                    2. Urgency (High/Medium/Low)
                    3. Date and Time

                    Respond in JSON format like:
                    {
                      "task": "...",
                      "urgency": "...",
                      "datetime": "..." (YYYY-MM-DD in this format. if not mentioned, make it 1 month from today. if mentioned today or tomorrow or similar, calculate and return in my desired format)
                    }

                    Take a note that today date is ${getTodayDate()}

                    Please keep the task field case-insensitive for comparison purposes.`
        },
        {
          role: "user",
          content: command
        }
      ],
      model: "openai/gpt-4.1",
      temperature: 1,
      max_tokens: 4096,
      top_p: 1
    });

    if (!response.choices || response.choices.length === 0) {
      console.error("Invalid response from OpenAI API");
      return null;
    }

    return response.choices[0].message.content;

} catch (error) {
    console.error("Error calling OpenAI API:", error);
    return null;
}
}