import json
import sys
import csv

def analyze_trivy_results(scan_file):
    try:
        # Load the Trivy scan results from the provided JSON file
        with open(scan_file, 'r') as file:
            data = json.load(file)

        # Check if the JSON structure contains vulnerabilities
        if 'Results' not in data:
            print("No vulnerability results found in the scan.")
            return

        # Extract and process vulnerabilities from the scan results
        vulnerabilities = []
        for result in data['Results']:
            if 'Vulnerabilities' in result:
                for vuln in result['Vulnerabilities']:
                    vulnerability = {
                        "VulnerabilityID": vuln.get('VulnerabilityID', 'N/A'),
                        "PkgName": vuln.get('PkgName', 'N/A'),
                        "InstalledVersion": vuln.get('InstalledVersion', 'N/A'),
                        "FixedVersion": vuln.get('FixedVersion', 'N/A'),
                        "Severity": vuln.get('Severity', 'N/A'),
                        "CVSS": vuln.get('CVSS', {}).get('bitnami', {}).get('V3Score', 'N/A'),
                        "PrimaryURL": vuln.get('PrimaryURL', 'N/A'),
                        "Description": vuln.get('Description', 'N/A')
                    }
                    vulnerabilities.append(vulnerability)

        # Print out the vulnerabilities and write to CSV
        if vulnerabilities:
            print(f"Found {len(vulnerabilities)} vulnerabilities:")
            for vuln in vulnerabilities:
                print(f"\nVulnerability ID: {vuln['VulnerabilityID']}")
                print(f"Package Name: {vuln['PkgName']}")
                print(f"Installed Version: {vuln['InstalledVersion']}")
                print(f"Fixed Version: {vuln['FixedVersion']}")
                print(f"Severity: {vuln['Severity']}")
                print(f"CVSS Score: {vuln['CVSS']}")
                print(f"More Info: {vuln['PrimaryURL']}")
                print(f"Description: {vuln['Description']}")

            # Write vulnerabilities to CSV
            csv_file = 'anomalous_vulnerabilities.csv'
            with open(csv_file, 'w', newline='') as csvfile:
                fieldnames = ['VulnerabilityID', 'PkgName', 'InstalledVersion', 'FixedVersion', 'Severity', 'CVSS', 'PrimaryURL', 'Description']
                writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
                writer.writeheader()
                for vuln in vulnerabilities:
                    writer.writerow(vuln)
            print(f"Vulnerabilities written to {csv_file}")
        else:
            print("No vulnerabilities found in the scan.")

    except json.JSONDecodeError:
        print("Error: Unable to parse the provided JSON.")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python analyze_trivy_results.py <path_to_trivy_scan.json>")
        sys.exit(1)

    scan_file = sys.argv[1]
    analyze_trivy_results(scan_file)