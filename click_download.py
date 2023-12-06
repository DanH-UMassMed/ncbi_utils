from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Replace with the path to your Chrome WebDriver executable
webdriver_path = '/Users/dan/bin/chromedriver'

# Set Chrome options if needed
chrome_options = webdriver.ChromeOptions()
chrome_options.add_argument("--headless=new")
# Add any additional options here if required

# Initialize the Chrome WebDriver
driver = webdriver.Chrome(executable_path=webdriver_path, options=chrome_options)

# URL of the web page
url = "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8322431/"

try:
    # Go to the web page
    driver.get(url)

    # Find the <li> tag with class="pdf-link other_item"
    pdf_link = WebDriverWait(driver, 15).until(EC.element_to_be_clickable((By.CSS_SELECTOR, "li.pdf-link.other_item")))

    # Click the PDF link
    pdf_link.click()

    # Switch to the new window/tab
    driver.switch_to.window(driver.window_handles[1])

    # Wait for a short time for the new page to load (you can adjust this timeout as needed)
    driver.implicitly_wait(5)

    # Find the button with id="secondaryDownload"
    save_button = WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.ID, "secondaryDownload")))

    # Click the Save button
    save_button.click()

except Exception as e:
    print(f"An error occurred: {e}")

# Do not call driver.quit() here to keep the browser window open
# The browser window will remain open after the script execution ends
