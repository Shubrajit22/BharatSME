# BharatSME

## Description
A brief description of the BharatSME project.

## Features
- Feature 1
- Feature 2

## Installation
Instructions on how to install the project.

## Usage
How to use the project.

## Contributing
Guidelines for contributing to the project.

## License
This project is licensed under the MIT License.


## STEPS FOR RUNNING THE APPLICATION
1. First create a virtual environment using this command:

```
python -m venv venv
```

2. Then install piptools to compile the requirements.in and compile the requirements

```
python -m pip install pip-tools
python -m piptools compile .\requirements.in
```

3. And then install the dependencies

```
python -m piptools sync .\requirements.txt 
```

4. Run the application

```
python -m uvicorn app.main:app --reload
```